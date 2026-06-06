package com.example.attendance.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class AttendanceReportExcelBuilder {

    private static final int COLUMN_COUNT = 3;

    private AttendanceReportExcelBuilder() {
    }

    public static byte[] buildReport(
            String sheetName,
            String reportTitle,
            String periodHeading,
            List<Object[]> rows
    ) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            XSSFCellStyle titleStyle = createTitleStyle(workbook);
            XSSFCellStyle periodStyle = createPeriodStyle(workbook);
            XSSFCellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook, false);
            CellStyle alternateDataStyle = createDataStyle(workbook, true);
            CellStyle numberStyle = createNumberStyle(workbook, false);
            CellStyle alternateNumberStyle = createNumberStyle(workbook, true);

            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(28);
            fillMergedRow(titleRow, reportTitle, titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, COLUMN_COUNT - 1));

            Row periodRow = sheet.createRow(1);
            periodRow.setHeightInPoints(22);
            fillMergedRow(periodRow, periodHeading, periodStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, COLUMN_COUNT - 1));

            Row headerRow = sheet.createRow(2);
            headerRow.setHeightInPoints(20);
            createStyledCell(headerRow, 0, "S.No.", headerStyle);
            createStyledCell(headerRow, 1, "Name", headerStyle);
            createStyledCell(headerRow, 2, "Days Present", headerStyle);

            int rowIndex = 3;
            int serialNumber = 1;
            for (Object[] row : rows) {
                boolean alternate = (serialNumber % 2) == 0;
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.setHeightInPoints(18);

                Cell serialCell = dataRow.createCell(0);
                serialCell.setCellValue(serialNumber++);
                serialCell.setCellStyle(alternate ? alternateNumberStyle : numberStyle);

                Cell nameCell = dataRow.createCell(1);
                nameCell.setCellValue(row[0] == null ? "" : row[0].toString());
                nameCell.setCellStyle(alternate ? alternateDataStyle : dataStyle);

                Cell daysCell = dataRow.createCell(2);
                daysCell.setCellValue(row[1] == null ? 0 : ((Number) row[1]).intValue());
                daysCell.setCellStyle(alternate ? alternateNumberStyle : numberStyle);
            }

            sheet.setColumnWidth(0, 3200);
            sheet.setColumnWidth(1, 9800);
            sheet.setColumnWidth(2, 5200);
            sheet.createFreezePane(0, 3);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to build attendance Excel report", ex);
        }
    }

    private static XSSFCellStyle createTitleStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0x1F, (byte) 0x4E, (byte) 0x78}, new DefaultIndexedColorMap()));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorder(style);
        return style;
    }

    private static XSSFCellStyle createPeriodStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xD9, (byte) 0xE8, (byte) 0xF7}, new DefaultIndexedColorMap()));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorder(style);
        return style;
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xE8, (byte) 0xEF, (byte) 0xF5}, new DefaultIndexedColorMap()));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorder(style);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook, boolean alternate) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (alternate) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        applyBorder(style);
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook, boolean alternate) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (alternate) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        applyBorder(style);
        return style;
    }

    private static void applyBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private static void fillMergedRow(Row row, String value, CellStyle style) {
        for (int column = 0; column < COLUMN_COUNT; column++) {
            createStyledCell(row, column, column == 0 ? value : "", style);
        }
    }

    private static void createStyledCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
