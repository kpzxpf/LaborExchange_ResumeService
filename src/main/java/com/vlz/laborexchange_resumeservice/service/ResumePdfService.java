package com.vlz.laborexchange_resumeservice.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.vlz.laborexchange_resumeservice.dto.WorkExperienceDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ResumePdfService {

    private final ResumeRepository resumeRepository;
    private final EducationService educationService;
    private final WorkExperienceService workExperienceService;
    private final SkillRetryClient skillRetryClient;

    private static final Color INDIGO        = new Color(79, 70, 229);
    private static final Color HEADER_BG     = new Color(238, 242, 255);
    private static final Color HEADER_BORDER = new Color(199, 210, 254);
    private static final Color TEXT_PRIMARY  = new Color(15, 23, 42);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER        = new Color(226, 232, 240);

    private static final String[] MONTHS_SHORT = {
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    };

    @Transactional(readOnly = true)
    public byte[] generatePdf(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        List<Education> education = educationService.getByResumeId(resumeId);
        List<WorkExperienceDto> workExp = workExperienceService.getByResume(resumeId);
        Set<String> skillNames = skillRetryClient.getNameSkillsByIds(
                resume.getSkillIds() != null ? new ArrayList<>(resume.getSkillIds()) : List.of()
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            BaseFont bfRegular = BaseFont.createFont(
                    "/com/lowagie/text/pdf/fonts/FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bfBold = BaseFont.createFont(
                    "/com/lowagie/text/pdf/fonts/FreeSansBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font nameFont      = new Font(bfBold,    22, Font.NORMAL, TEXT_PRIMARY);
            Font titleFont     = new Font(bfRegular, 14, Font.NORMAL, INDIGO);
            Font sectionFont   = new Font(bfBold,    9,  Font.NORMAL, TEXT_MUTED);
            Font bodyFont      = new Font(bfRegular, 10, Font.NORMAL, TEXT_PRIMARY);
            Font bodyBold      = new Font(bfBold,    10, Font.NORMAL, TEXT_PRIMARY);
            Font accentFont    = new Font(bfRegular, 10, Font.NORMAL, INDIGO);
            Font mutedFont     = new Font(bfRegular, 9,  Font.NORMAL, TEXT_MUTED);
            Font contactFont   = new Font(bfRegular, 9,  Font.NORMAL, TEXT_MUTED);

            addHeader(doc, resume, nameFont, titleFont, contactFont);
            addSummary(doc, resume, sectionFont, bodyFont);
            addSkills(doc, skillNames, sectionFont, accentFont);
            addWorkExperience(doc, workExp, sectionFont, bodyFont, bodyBold, accentFont, mutedFont);
            addEducation(doc, education, sectionFont, bodyFont, bodyBold, accentFont, mutedFont);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for resume " + resumeId, e);
        }
    }

    private void addHeader(Document doc, Resume resume, Font nameFont, Font titleFont, Font contactFont)
            throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(18);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(HEADER_BG);
        cell.setBorderColor(HEADER_BORDER);
        cell.setBorderWidth(1);
        cell.setPadding(20);

        String fullName = Stream.of(resume.getFirstName(), resume.getLastName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));

        if (!fullName.isBlank()) {
            Paragraph namePara = new Paragraph(fullName, nameFont);
            namePara.setSpacingAfter(4);
            cell.addElement(namePara);
        }

        Paragraph titlePara = new Paragraph(resume.getTitle(), titleFont);
        titlePara.setSpacingAfter(10);
        cell.addElement(titlePara);

        List<String> contact = new ArrayList<>();
        if (resume.getLocation() != null && !resume.getLocation().isBlank())
            contact.add(resume.getLocation());
        if (resume.getExperienceYears() != null)
            contact.add("Опыт: " + resume.getExperienceYears() + " " + yearLabel(resume.getExperienceYears()));
        if (resume.getContactEmail() != null && !resume.getContactEmail().isBlank())
            contact.add(resume.getContactEmail());
        if (resume.getContactPhone() != null && !resume.getContactPhone().isBlank())
            contact.add(resume.getContactPhone());

        if (!contact.isEmpty()) {
            cell.addElement(new Paragraph(String.join("   |   ", contact), contactFont));
        }

        table.addCell(cell);
        doc.add(table);
    }

    private void addSummary(Document doc, Resume resume, Font sectionFont, Font bodyFont)
            throws DocumentException {
        if (resume.getSummary() == null || resume.getSummary().isBlank()) return;
        addSectionTitle(doc, "О СЕБЕ", sectionFont);
        Paragraph p = new Paragraph(resume.getSummary(), bodyFont);
        p.setLeading(15);
        p.setSpacingAfter(14);
        doc.add(p);
    }

    private void addSkills(Document doc, Set<String> skillNames, Font sectionFont, Font accentFont)
            throws DocumentException {
        if (skillNames.isEmpty()) return;
        addSectionTitle(doc, "НАВЫКИ", sectionFont);
        Paragraph p = new Paragraph(String.join("  •  ", skillNames), accentFont);
        p.setSpacingAfter(14);
        doc.add(p);
    }

    private void addWorkExperience(Document doc, List<WorkExperienceDto> workExp,
                                   Font sectionFont, Font bodyFont, Font bodyBold,
                                   Font accentFont, Font mutedFont) throws DocumentException {
        if (workExp.isEmpty()) return;
        addSectionTitle(doc, "ОПЫТ РАБОТЫ", sectionFont);

        for (WorkExperienceDto w : workExp) {
            PdfPTable row = new PdfPTable(new float[]{3.5f, 1.5f});
            row.setWidthPercentage(100);
            row.setSpacingAfter(8);

            PdfPCell left = new PdfPCell();
            left.setBorder(Rectangle.LEFT);
            left.setBorderColor(INDIGO);
            left.setBorderWidth(2);
            left.setPaddingLeft(10);
            left.setPaddingBottom(8);
            left.addElement(new Paragraph(w.getPosition(), bodyBold));
            left.addElement(new Paragraph(w.getCompanyName(), accentFont));
            if (w.getDescription() != null && !w.getDescription().isBlank()) {
                Paragraph desc = new Paragraph(w.getDescription(), bodyFont);
                desc.setLeading(14);
                left.addElement(desc);
            }

            PdfPCell right = new PdfPCell();
            right.setBorder(Rectangle.NO_BORDER);
            right.setPaddingBottom(8);
            Paragraph period = new Paragraph(formatPeriod(w), mutedFont);
            period.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(period);
            if (Boolean.TRUE.equals(w.getIsCurrent())) {
                Paragraph cur = new Paragraph("Текущее место", accentFont);
                cur.setAlignment(Element.ALIGN_RIGHT);
                right.addElement(cur);
            }

            row.addCell(left);
            row.addCell(right);
            doc.add(row);
        }
    }

    private void addEducation(Document doc, List<Education> education,
                               Font sectionFont, Font bodyFont, Font bodyBold,
                               Font accentFont, Font mutedFont) throws DocumentException {
        if (education.isEmpty()) return;
        addSectionTitle(doc, "ОБРАЗОВАНИЕ", sectionFont);

        for (Education edu : education) {
            PdfPTable row = new PdfPTable(new float[]{3.5f, 1.5f});
            row.setWidthPercentage(100);
            row.setSpacingAfter(8);

            PdfPCell left = new PdfPCell();
            left.setBorder(Rectangle.LEFT);
            left.setBorderColor(BORDER);
            left.setBorderWidth(2);
            left.setPaddingLeft(10);
            left.setPaddingBottom(8);
            left.addElement(new Paragraph(edu.getDegree(), bodyBold));
            left.addElement(new Paragraph(edu.getInstitution(), accentFont));
            if (edu.getFieldOfStudy() != null && !edu.getFieldOfStudy().isBlank()) {
                left.addElement(new Paragraph(edu.getFieldOfStudy(), bodyFont));
            }

            PdfPCell right = new PdfPCell();
            right.setBorder(Rectangle.NO_BORDER);
            right.setPaddingBottom(8);
            String years = edu.getStartYear() + " — " + (edu.getEndYear() != null ? edu.getEndYear() : "наст. время");
            Paragraph yearsPara = new Paragraph(years, mutedFont);
            yearsPara.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(yearsPara);

            row.addCell(left);
            row.addCell(right);
            doc.add(row);
        }
    }

    private void addSectionTitle(Document doc, String title, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(8);

        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BORDER);
        cell.setBorderWidth(1);
        cell.setPaddingBottom(5);
        table.addCell(cell);
        doc.add(table);
    }

    private String formatPeriod(WorkExperienceDto w) {
        StringBuilder sb = new StringBuilder();
        if (w.getStartMonth() != null && w.getStartMonth() >= 1 && w.getStartMonth() <= 12)
            sb.append(MONTHS_SHORT[w.getStartMonth() - 1]).append(" ");
        if (w.getStartYear() != null) sb.append(w.getStartYear());
        sb.append(" — ");
        if (Boolean.TRUE.equals(w.getIsCurrent())) {
            sb.append("наст. вр.");
        } else {
            if (w.getEndMonth() != null && w.getEndMonth() >= 1 && w.getEndMonth() <= 12)
                sb.append(MONTHS_SHORT[w.getEndMonth() - 1]).append(" ");
            if (w.getEndYear() != null) sb.append(w.getEndYear());
        }
        return sb.toString();
    }

    private String yearLabel(int years) {
        int mod10 = years % 10;
        int mod100 = years % 100;
        if (mod10 == 1 && mod100 != 11) return "год";
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) return "года";
        return "лет";
    }
}
