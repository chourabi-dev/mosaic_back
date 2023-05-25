package Main.Controller;

import Main.Beans.ConfigItem;
import Main.Beans.NewConfigRequest;
import Main.Beans.NewImageUpdateRequest;
import Main.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "Admin")
public class AdminController {

    @Autowired
    AdminService service;
    
    
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("hello world");
    }
    

    @RequestMapping(value = "/addNewConfiguration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> AddNewConfiguration(@RequestBody NewConfigRequest item) {
        return service.AddNewConfiguration(item);
    }

    @RequestMapping(value = "/fetchConfigurations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConfigItem>> fetchConfigurations() {
        return service.fetchConfigurations();
    }

    @RequestMapping(value = "/checkDataServiceConnectivity", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkDataServiceConnectivity() {
        return service.checkDataServiceConnectivity();
    }

    @RequestMapping(value = "/updateConfiguration", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateConfiguration(@RequestBody ConfigItem item) {
        return service.updateConfiguration(item);
    }

    @RequestMapping(value = "/deleteConfiguration/{collectionname}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteConfiguration(@PathVariable("collectionname") String collectionname) {
        return service.deleteConfiguration(collectionname);
    }

    @RequestMapping(value = "/deleteCaches/{collectionname}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteCaches(@PathVariable("collectionname") String collectionname) {
        return service.deleteCaches(collectionname);
    }

    @RequestMapping(value = "/deleteAllImages/{collectionname}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteAllImages(@PathVariable("collectionname") String collectionname) {
        return service.deleteAllImages(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/updateLogoImage", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateLogoImage(@PathVariable("collectionname") String collectionname, @RequestBody NewImageUpdateRequest body) {
        return service.updateLogoImage(collectionname, body);
    }

    @RequestMapping(value = "/{collectionname}/updateBannerImage", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateBannerImage(@PathVariable("collectionname") String collectionname, @RequestBody NewImageUpdateRequest body) {
        return service.updateBannerImage(collectionname, body);
    }

    @RequestMapping(value = "/{collectionname}/updateHeaderImage", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateHeaderImage(@PathVariable("collectionname") String collectionname, @RequestBody NewImageUpdateRequest body) {
        return service.updateHeaderImage(collectionname, body);
    }

    @RequestMapping(value = "/{collectionname}/deleteItem/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteItem(@PathVariable("collectionname") String collectionname, @PathVariable("uuid") String uuid) {
        return service.deleteItem(collectionname, uuid);
    }
}
