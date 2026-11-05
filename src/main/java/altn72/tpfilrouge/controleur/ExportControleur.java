package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.ExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Lot alpha - piste 9 : export CSV de l'historique des incidents résolus (F9 étendue).
@Controller
public class ExportControleur {

    private final ExportService exportService;

    public ExportControleur(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/historique/export")
    public ResponseEntity<byte[]> exporterHistorique() {
        byte[] csv = exportService.genererCsvHistorique();
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("historique-incidents.csv")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv);
    }
}
