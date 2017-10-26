/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.Builder;
import lombok.Value;
import org.assemblits.eru.entities.Alarm;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.historian.HistoricDao;
import org.assemblits.eru.persistence.AlarmRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static org.assemblits.eru.util.PdfReportCreator.Report.*;

@Component
public class PdfReportCreator {

    private static final String REPORT_TITLE = "POWER SCENE VIEWER";
    private static final String REPORT_CREATOR = "Power Scene Viewer - ";
    private static final String REPORT_KEYWORD = "Report";
    private static final Font TITLE_FONT = new Font(Font.NORMAL, 9, Font.BOLD);
    private static final Font TABLE_TITLE_FONT = new Font(Font.NORMAL, 9, Font.BOLD);
    private static final Font VALUE_FONT = new Font(Font.NORMAL, 8, Font.TIMES_ROMAN);
    private final HistoricDao historicDAO;
    private final AlarmRepository alarmRepository;

    public PdfReportCreator(HistoricDao historicDAO, AlarmRepository alarmRepository) {
        this.historicDAO = historicDAO;
        this.alarmRepository = alarmRepository;
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(""));
        }
    }

    @Transactional
    public void makePDF(Configuration configuration, String subject) throws Exception {

        Report reportType = Optional.ofNullable(configuration.getReportType()).orElse(ALL_TAGS);
        String alarmGroup = Optional.ofNullable(configuration.getAlarmGroup()).orElse("None");
        LocalDate initDate = Optional.ofNullable(configuration.getInitDate()).orElse(LocalDate.now());
        LocalDate finalDate = Optional.ofNullable(configuration.getFinalDate()).orElse(LocalDate.now());
        List<Tag> tags = Optional.ofNullable(configuration.getTags()).orElse(new LinkedList<>());
        String author = Optional.ofNullable(configuration.getAuthor()).orElse("");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(configuration.getFileName()));
        document.open();
        // Metadata
        document.addTitle(REPORT_TITLE);
        document.addKeywords(REPORT_KEYWORD);
        document.addCreator(REPORT_CREATOR);
        document.addSubject(subject.isEmpty() ? "" : subject);
        document.addAuthor(author);

        // Preface
        Paragraph preface = new Paragraph();

        addEmptyLine(preface, 1);
//            Paragraph title = new Paragraph(App.CLIENT);
        Paragraph title = new Paragraph("Power Scene Viewer report - #" + System.currentTimeMillis());
        title.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph date = new Paragraph("Date: " + new Date(), TITLE_FONT);
        date.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph authorParagraph = new Paragraph("Author: " + author, TITLE_FONT);
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
        if (reportType.equals(ALL_ALARMS) || reportType.equals(Report.DATE_FILTER_ALARMS)) {
            table = new PdfPTable(6);
            table.setWidthPercentage(100);
            String[] columnTitles = {"Date", "Description", "Acknowledge-By", "EruGroup", "Acknowledge-Status", "Priority"};
            for (String columnTitle : columnTitles) {
                PdfPCell cell = new PdfPCell(new Phrase(columnTitle, TABLE_TITLE_FONT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            fillTable(table, reportType, tags, initDate, finalDate, alarmGroup);
            paragraph.add(table);
        } else if (reportType.equals(ALL_TAGS) || reportType.equals(DATE_FILTER_TAGS)) {
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

            fillTable(table, reportType, tags, initDate, finalDate, alarmGroup);
            paragraph.add(table);
        }

        document.add(paragraph);
        document.close();
    }

    private void fillTable(PdfPTable table, Report reportType, List<Tag> tags, LocalDate initDate, LocalDate finalDate,
                           String alarmGroup) throws SQLException, InterruptedException {
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
                for (Alarm a : alarmRepository.findAllByOrderByTimeStampAsc()) {
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
                for (Alarm a : alarmRepository.findAllByOrderByTimeStampAsc()) {
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
    }

    private void writeTableRow(PdfPTable table, Alarm a) {
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

    public enum Report {ALL_TAGS, DATE_FILTER_TAGS, ALL_ALARMS, DATE_FILTER_ALARMS}

    @Value
    @Builder
    public static class Configuration {
        Report reportType;
        List<Tag> tags;
        LocalDate initDate;
        LocalDate finalDate;
        String alarmGroup;
        String fileName;
        String author;
    }
}
