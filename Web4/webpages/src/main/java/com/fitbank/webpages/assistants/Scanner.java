package com.fitbank.webpages.assistants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.scanner.ScanningJob;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.util.PDFHeader;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

/**
 * Asistente para llenar el campo con una imagen escaneada
 *
 * @author Soft Warehouse S.A.
 */
public class Scanner extends File {

    private static final long serialVersionUID = 1L;

    @XML(ignore = true)
    private static final int MAX_IMAGE_RETRIEVE_TRIES = 3;

    @XML(ignore = true)
    private static final String TIFF_HEADER_BASE64 = "SUkqA";

    /**
     * PDDocument en cache si se ha usado.
     */
    @XML(ignore = true)
    private transient PdfDecoder cachedDoc = null;

    /**
     * Usado como referencia para saber si el cache del doc es válido.
     */
    @XML(ignore = true)
    private transient int valueHash = 0;

    @Editable
    private ScanningJob scanningJob = new ScanningJob();

    public ScanningJob getScanningJob() {
        return scanningJob;
    }

    public void setScanningJob(ScanningJob scanningJob) {
        this.scanningJob = scanningJob;
    }

    @Override
    public boolean readFromHttpRequest() {
        return true;
    }

    public synchronized Object asImage(String value, int pageNumber) {
        if (StringUtils.isBlank(value)) {
            try {
                return IOUtils.toByteArray(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(
                                "com/fitbank/webpages/img/blanco.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (value.startsWith(PDFHeader.BASE64)) {
            try {
                this.getDocument(value);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int tries = 0;

                //Método de extracción sin mucha compresión.
                cachedDoc.setExtractionMode(7, 1.5F);

                BufferedImage img = this.cachedDoc.getPageAsImage(pageNumber + 1);
                while (img == null && tries < Scanner.MAX_IMAGE_RETRIEVE_TRIES) {
                    Thread.sleep(250);
                    img = this.cachedDoc.getPageAsImage(pageNumber + 1);

                    tries++;
                }

                if (img != null) {
                    ImageIO.write(img, "jpg", baos);

                    return baos.toByteArray();
                }

                Debug.error("Esto no deberia pasar. Imagen no pudo ser leida, cargando default");
                return IOUtils.toByteArray(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("com/fitbank/webpages/img/blanco.png"));
            } catch (IOException e) {
                Debug.error(e);
                return new byte[0];
            } catch (PdfException e) {
                Debug.error(e);
                return new byte[0];
            } catch (InterruptedException e) {
                Debug.error(e);
                return new byte[0];
            }
        } else if (value.startsWith(TIFF_HEADER_BASE64)) {
            ByteArrayOutputStream baos = this.convertTiffToPdf(value);
            String pdfEncoded = Base64.encodeBase64String(baos.toByteArray());
            return this.asImage(pdfEncoded, pageNumber);
        } else {
            return asObject(value);
        }
    }

    private void getDocument(String value) throws IOException, PdfException {
        if (value.startsWith(PDFHeader.BASE64)) {
            if (cachedDoc == null || value.hashCode() != valueHash) {
                byte[] data = Base64.decodeBase64(value);

                cachedDoc = new PdfDecoder();
                cachedDoc.openPdfArray(data);
                valueHash = value.hashCode();
            }
        } else if (value.startsWith(TIFF_HEADER_BASE64)) {
            ByteArrayOutputStream baos = this.convertTiffToPdf(value);
            String pdfEncoded = Base64.encodeBase64String(baos.toByteArray());
            this.getDocument(pdfEncoded);
        } else {
            cachedDoc = null;
            valueHash = 0;
        }
    }

    public int getNumberOfPages(String value) {
        try {
            this.getDocument(value);

            if (this.cachedDoc != null) {
                return this.cachedDoc.getPageCount();
            }
        } catch (IOException e) {
            Debug.error(e);
        } catch (PdfException e) {
            Debug.error(e);
        }

        return 1;
    }

    /**
     * Convertir una imagen TIFF a un PDF para ser visualizado como tal en el
     * Frontend
     *
     * @param value Representacion de la imagen en Base64
     * @return Stream de la imagen en PDF
     * @throws IOException
     * @throws DocumentException
     */
    private ByteArrayOutputStream convertTiffToPdf(String value) {
        byte[] data = Base64.decodeBase64(value);
        ByteArrayInputStream imgBais = new ByteArrayInputStream(data);

        Image image;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setStrictImageSequence(true);
            document.open();

            RandomAccessFileOrArray ra = new RandomAccessFileOrArray(imgBais);
            int pages = TiffImage.getNumberOfPages(ra);
            for (int i = 1; i <= pages; i++) {
                image = TiffImage.getTiffImage(ra, i);
                image.setAbsolutePosition(0, 0);
                image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                document.setPageSize(PageSize.A4);
                document.newPage();
                document.add(image);
            }

            document.close();
            out.flush();

        } catch (DocumentException e) {
            Debug.error("Problemas al crear un documento PDF", e);
        } catch (IOException e) {
            Debug.error("Problemas al leer el contenido del archivo TIFF", e);
        }

        return out;
    }
}
