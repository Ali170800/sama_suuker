package com.suivi.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.suivi.dao.GlycemieDao;
import com.suivi.model.Compte;
import com.suivi.model.Glycemie;
import com.suivi.model.Insuline;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/glycemie/pdf")
public class PdfExportServlet extends HttpServlet {
    private GlycemieDao glycemieDao = new GlycemieDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Compte compte = (session != null) ? (Compte) session.getAttribute("compteConnecte") : null;

        if (compte == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // --- Récupération de l'EntityManager ouvert par le filtre ---
        EntityManager em = (EntityManager) request.getAttribute("em");

        // Utilisation de cet EntityManager pour appeler la méthode du DAO
        List<Glycemie> glycemies = glycemieDao.listerParCompte(em, compte.getId());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Rapport_Medical_Glycemie.pdf");

        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // --- 1. EN-TÊTE DU DOCUMENT ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(13, 110, 253));
            Paragraph title = new Paragraph("RAPPORT DE SUIVI GLYCÉMIQUE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.GRAY);
            Paragraph subTitle = new Paragraph("Document médical destiné au corps médical", subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingAfter(20);
            document.add(subTitle);

            // --- 2. INFORMATIONS PATIENT ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            String nomPatient = (compte.getNom() != null ? compte.getNom() : "") + " " + (compte.getPrenom() != null ? compte.getPrenom() : "");
            if(nomPatient.trim().isEmpty()) {
                nomPatient = compte.getEmail();
            }

            PdfPCell cellPatient = new PdfPCell(new Phrase("Patient : " + nomPatient.trim(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY)));
            cellPatient.setBorder(Rectangle.NO_BORDER);
            cellPatient.setPadding(5);
            infoTable.addCell(cellPatient);

            PdfPCell cellDate = new PdfPCell(new Phrase("Date d'édition : " + java.time.LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY)));
            cellDate.setBorder(Rectangle.NO_BORDER);
            cellDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellDate.setPadding(5);
            infoTable.addCell(cellDate);

            document.add(infoTable);

            com.itextpdf.text.pdf.draw.LineSeparator separator = new com.itextpdf.text.pdf.draw.LineSeparator();
            separator.setLineColor(new BaseColor(200, 200, 200));
            document.add(new Chunk(separator));
            document.add(new Paragraph(" "));

            // --- 3. TABLEAU DES MESURES ---
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.2f, 2.2f, 2.5f, 2.2f, 3.2f});
            table.setSpacingAfter(20);

            addCellHeader(table, "Date & Heure");
            addCellHeader(table, "Glycémie");
            addCellHeader(table, "Contexte / Moment");
            addCellHeader(table, "Insuline (Unités)");
            addCellHeader(table, "Type d'Insuline");

            Font boldDataFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);

            for (Glycemie g : glycemies) {
                PdfPCell c1 = new PdfPCell(new Phrase(g.getDateHeureFormatee() != null ? g.getDateHeureFormatee() : "-", boldDataFont));
                c1.setPadding(6);
                table.addCell(c1);

                double val = g.getValeur();
                Font valFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
                BaseColor bgColor = BaseColor.WHITE;

                if (val < 0.70) {
                    valFont.setColor(new BaseColor(220, 53, 69));
                    bgColor = new BaseColor(253, 237, 238);
                } else if (val > 1.80) {
                    valFont.setColor(new BaseColor(255, 140, 0));
                    bgColor = new BaseColor(255, 243, 205);
                } else {
                    valFont.setColor(new BaseColor(40, 167, 69));
                    bgColor = new BaseColor(235, 247, 238);
                }

                PdfPCell c2 = new PdfPCell(new Phrase(val + " g/L", valFont));
                c2.setPadding(6);
                c2.setBackgroundColor(bgColor);
                table.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Phrase(g.getType() != null ? g.getType() : "-", boldDataFont));
                c3.setPadding(6);
                table.addCell(c3);

                StringBuilder unitesBuilder = new StringBuilder();
                StringBuilder nomsBuilder = new StringBuilder();

                if (g.getInsulines() != null && !g.getInsulines().isEmpty()) {
                    for (Insuline ins : g.getInsulines()) {
                        unitesBuilder.append(ins.getUnite()).append(" U\n");
                        nomsBuilder.append(ins.getType() != null ? ins.getType() : "").append("\n");
                    }
                }

                PdfPCell c4 = new PdfPCell(new Phrase(unitesBuilder.toString().trim(), boldDataFont));
                c4.setPadding(6);
                table.addCell(c4);

                PdfPCell c5 = new PdfPCell(new Phrase(nomsBuilder.toString().trim(), boldDataFont));
                c5.setPadding(6);
                table.addCell(c5);
            }

            document.add(table);

            // --- 4. LA COURBE ---
            String chartDataString = request.getParameter("chartImage");
            if (chartDataString != null && chartDataString.startsWith("data:image/png;base64,")) {
                try {
                    String base64Image = chartDataString.substring("data:image/png;base64,".length());
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                    Image chartImage = Image.getInstance(imageBytes);
                    chartImage.scaleToFit(500, 210);
                    chartImage.setAlignment(Element.ALIGN_CENTER);

                    Paragraph graphTitle = new Paragraph("Courbe d'évolution de la glycémie", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY));
                    graphTitle.setSpacingAfter(6);
                    document.add(graphTitle);

                    document.add(chartImage);
                    document.add(new Paragraph(" "));
                } catch (Exception e) {
                    // Ignorer en cas d'erreur de conversion
                }
            }

            // --- 5. LÉGENDE ---
            document.add(new Paragraph(" "));
            Font legendFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph legend = new Paragraph("Légende : 🟢 Glycémie normale | 🟠 Hyperglycémie (> 1.80 g/L) | 🔴 Hypoglycémie (< 0.70 g/L)", legendFont);
            legend.setAlignment(Element.ALIGN_CENTER);
            document.add(legend);

            document.close();

        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du rapport PDF", e);
        }
        // L'EntityManager sera fermé automatiquement par le filtre EntityManagerFilter à la fin de la requête.
    }

    private void addCellHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setBackgroundColor(new BaseColor(52, 58, 64));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }
}