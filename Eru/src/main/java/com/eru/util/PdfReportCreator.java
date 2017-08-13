package com.eru.util;

import com.eru.alarming.Alarm;
import com.eru.historian.HistoricDao;
import com.eru.tag.Tag;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.eru.persistence.Dao;

import javax.persistence.EntityManager;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FXML Controller class
 *
 * Title: PdfReportCreator.java <br>
 * Company: Comelecinca Power Systems C.A.<br>
 *
 * @author Rafael Solorzano (RS), Lucio Guerchi (LG), Marlon Trujillo (MT)
 * @version 1.0
 * <br><br>
 * Changes:<br>
 * <ul>
 * <li> 2012-08-21 (RS) Creation </li>
 * <li> 2013-08-21 (LG) Re-factor </li>
 * <li> 2014-07-17 (MT) Re-factor </li>
 * <ul>
 */
public class PdfReportCreator {

    /* ********** Static Fields ********** */
    public static enum Report {ALL_TAGS, DATE_FILTER_TAGS, ALL_ALARMS, DATE_FILTER_ALARMS}
    private static final String     REPORT_TITLE     = "POWER SCENE VIEWER";
    private static final String     REPORT_CREATOR   = "Power Scene Viewer - ";
    private static final String     REPORT_KEYWORD   = "Report";
    private static final Font       TITLE_FONT       = new Font(Font.NORMAL, 9, Font.BOLD);
    private static final Font       TABLE_TITLE_FONT = new Font(Font.NORMAL, 9, Font.BOLD);
    private static final Font       VALUE_FONT       = new Font(Font.NORMAL, 8, Font.TIMES_ROMAN);


    /* ********** Fields ********** */
    private Report    reportType = Report.ALL_TAGS;
    private List<Tag> tags       = new LinkedList<>();
    private LocalDate initDate   = LocalDate.now();
    private LocalDate finalDate  = LocalDate.now();
    private String    alarmGroup = "None";
    private String    fileName;
    private String    author;


    /* ********** Constructor ********** */
    public PdfReportCreator() {
    }


    /* ********** Methods ********** */
    public void makePDF(String subject) throws Exception {
        // This line save the exportation for a null user
        author = author == null ? "" : author;

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        // Metadata
        document.addTitle(REPORT_TITLE);
        document.addKeywords(REPORT_KEYWORD);
        document.addCreator(REPORT_CREATOR);
        document.addSubject(subject.isEmpty() ? "" : subject);
        document.addAuthor(author.isEmpty() ? "" : author);

        // Preface
        Paragraph preface = new Paragraph();

        addEmptyLine(preface, 1);
//            Paragraph title = new Paragraph(App.CLIENT);
        Paragraph title = new Paragraph("Power Scene Viewer report - #" + System.currentTimeMillis());
        title.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph date = new Paragraph("Date: " + new Date(), TITLE_FONT);
        date.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph authorParagraph = new Paragraph("Author: " + (author.isEmpty() ? "" : author), TITLE_FONT);
        authorParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph range = new Paragraph("Report from " + initDate + " to " + finalDate, TITLE_FONT);
        range.setAlignment(Paragraph.ALIGN_CENTER);

        //TITLE
        preface.add(title);

        //DATE
        preface.add(date);

        //AUTHOR
        preface.add(authorParagraph);

        //RANGE
        preface.add(range);

        //FINISH
        addEmptyLine(preface, 1);
        document.add(preface);

        // JUMP PAGE
        //document.newPage();

        // TABLE
        Paragraph paragraph = new Paragraph();
        PdfPTable table;
        if(reportType.equals(Report.ALL_ALARMS) || reportType.equals(Report.DATE_FILTER_ALARMS)){
            REPORT_CREATOR.concat("ALARM REPORT");
            table = new PdfPTable(6);
            table.setWidthPercentage(100);
            String[] columnTitles = {"Date", "Description","Acknowledge-By","TreeElementsGroup","Acknowledge-Status","Priority"};
            for(String columnTitle: columnTitles){
                PdfPCell cell = new PdfPCell(new Phrase(columnTitle, TABLE_TITLE_FONT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            fillTable(table);
            paragraph.add(table);
        }else if(reportType.equals(Report.ALL_TAGS) || reportType.equals(Report.DATE_FILTER_TAGS)){
            REPORT_CREATOR.concat("HISTORIAN REPORT");
            table = new PdfPTable(tags.size() + 1);
            table.setWidthPercentage(100);
            for (Tag t : tags) {
                PdfPCell c = new PdfPCell(new Phrase(t.getName(), TABLE_TITLE_FONT));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c);
            }
            String columnFour = "Date";
            PdfPCell lastCell = new PdfPCell(new Phrase(columnFour));
            lastCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(lastCell);

            fillTable(table);
            paragraph.add(table);
        }

        document.add(paragraph);
        document.close();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(""));
        }
    }

    private void fillTable(PdfPTable table) throws SQLException, InterruptedException {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        HistoricDao historicDAO = new HistoricDao(em);
        Dao<Alarm> alarmDao = new Dao<>(em, Alarm.class);

        switch (reportType) {
            case ALL_TAGS:
                for (Map<String, String> m : historicDAO.getTagsHistoric(tags)) {
                    m.keySet();
                    for (Map.Entry<String, String> entry : m.entrySet()) {
                        writeTableCell(table, entry.getValue());
                    }
                }
                break;
            case DATE_FILTER_TAGS:
                for (Map<String, String> m : historicDAO.getTagsHistoric(tags, initDate, finalDate)) {
                    m.keySet();
                    for (Map.Entry<String, String> entry : m.entrySet()) {
                        writeTableCell(table, entry.getValue());
                    }
                }
                break;
            case ALL_ALARMS:
                for (Alarm a : alarmDao.findEntities("timeStamp", Dao.Order.ASC)) {
                    if (alarmGroup.equals("ALL")) {
                        writeTableRow(table, a);
                    } else {
                        if (a.getGroupName().equals(alarmGroup)) {
                            writeTableRow(table, a);
                        }
                    }
                }
                break;
            case DATE_FILTER_ALARMS:
                for (Alarm a : alarmDao.findEntities("timeStamp", Dao.Order.ASC)) {
                    if (a.getTimeStamp().toLocalDateTime().isBefore(finalDate.plusDays(1).atStartOfDay()) && a.getTimeStamp().toLocalDateTime().isAfter(initDate.atStartOfDay())) {
                        if (alarmGroup.equals("ALL")) {
                            writeTableRow(table, a);
                        } else {
                            if (a.getGroupName().equals(alarmGroup)) {
                                writeTableRow(table, a);
                            }
                        }
                    }
                }
                break;
        }
        em.close();
    }

    private void writeTableRow(PdfPTable table, Alarm a){
        writeTableCell(table, a.getTimeStamp().toString());
        writeTableCell(table, a.getDescription());
        writeTableCell(table, a.getUserInCharge());
        writeTableCell(table, a.getGroupName());
        writeTableCell(table, Boolean.toString(a.getAcknowledged()));
        writeTableCell(table, a.getPriority().toString());
    }

    private void writeTableCell(PdfPTable table, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(value, VALUE_FONT));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
    }


    /* ********** Setters and Getters ********** */
    public Report getReportType() {
        return reportType;
    }
    public void setReportType(Report reportType) {
        this.reportType = reportType;
    }

    public LocalDate getInitDate() {
        return initDate;
    }
    public void setInitDate(LocalDate initDate) {
        this.initDate = initDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getAlarmGroup() {
        return alarmGroup;
    }
    public void setAlarmGroup(String alarmGroup) {
        this.alarmGroup = alarmGroup;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
}
