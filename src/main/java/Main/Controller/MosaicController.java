package Main.Controller;

import Main.Beans.CollageItem;
import Main.Beans.ConfigItem;
import Main.Beans.NewImageRequest;
import Main.Service.MosaicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "Mosaic")
public class MosaicController {

    @Autowired
    MosaicService service;

    @RequestMapping(value = "/{collectionname}/L17/{imagename}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> L17_ImageHandler(@PathVariable("collectionname") String collectionname, @PathVariable("imagename") String imagename) throws IOException {
        return service.L17Service(collectionname, imagename);
    }

    @RequestMapping(value = "/{collectionname}/Live/{imagename}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> Live_ImageHandler(@PathVariable("collectionname") String collectionname, @PathVariable("imagename") String imagename) throws IOException {
        return service.LiveService(collectionname, imagename);
    }

    @RequestMapping(value = "/{collectionname}/L18/{imagename}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> L18_ImageHandler(@PathVariable("collectionname") String collectionname, @PathVariable("imagename") String imagename) throws IOException {
        return service.L18Service(collectionname, imagename);
    }

    @RequestMapping(value = "/{collectionname}/L19/{imagename}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> L19_ImageHandler(@PathVariable("collectionname") String collectionname, @PathVariable("imagename") String imagename) throws IOException {
        return service.L19Service(collectionname, imagename);
    }

    @RequestMapping(value = "/{collectionname}/L20/{imagename}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> L20_ImageHandler(@PathVariable("collectionname") String collectionname, @PathVariable("imagename") String imagename) throws IOException {
        return service.L20Service(collectionname, imagename);
    }

    @RequestMapping(value = "/{collectionname}/getItem/{col}/{row}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> getItem(@PathVariable("collectionname") String collectionname, @PathVariable("col") int col, @PathVariable("row") int row) {
        return service.getItem(collectionname, col, row);
    }

    @RequestMapping(value = "/{collectionname}/getImage/{col}/{row}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("collectionname") String collectionname, @PathVariable("col") int col, @PathVariable("row") int row) {
        return service.getImage(collectionname, col, row);
    }

    @RequestMapping(value = "/{collectionname}/{uuid}/Image.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("collectionname") String collectionname, @PathVariable("uuid") String uuid) {
        return service.getImage(collectionname, uuid);
    }

    @RequestMapping(value = "/{collectionname}/getAllItems", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CollageItem>> getAllItems(@PathVariable("collectionname") String collectionname) {
        return service.getAllItems(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/filterByMail/{mail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CollageItem>> filterByMail(@PathVariable("collectionname") String collectionname, @PathVariable("mail") String mail) {
        return service.filterByMail(collectionname, mail);
    }

    @RequestMapping(value = "/{collectionname}/filterByName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CollageItem>> filterByName(@PathVariable("collectionname") String collectionname, @PathVariable("name") String name) {
        return service.filterByName(collectionname, name);
    }

    @RequestMapping(value = "/{collectionname}/countItems", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> countItems(@PathVariable("collectionname") String collectionname) {
        return service.countItems(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/getConfig", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigItem> getConfig(@PathVariable("collectionname") String collectionname) {
        return service.getConfig(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/postNewImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> postNewImage(@PathVariable("collectionname") String collectionname, @RequestBody NewImageRequest body) {
        return service.postNewImage(collectionname, body);
    }

    @RequestMapping(value = "/{collectionname}/getItemByUuid/{uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> getItemByUuid(@PathVariable("collectionname") String collectionname, @PathVariable("uuid") String uuid) {
        return service.getItemByUuid(collectionname, uuid);
    }

    @RequestMapping(value = "/{collectionname}/getLastElement/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> getLastElement(@PathVariable("collectionname") String CollectionName) {
        return service.getLastElement(CollectionName);
    }

    @RequestMapping(value = "/{collectionname}/getItem/{col}/{row}/next", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> getItemNext(@PathVariable("collectionname") String collectionname, @PathVariable("col") int col, @PathVariable("row") int row) {
        return service.getItemNext(collectionname, col, row);
    }

    @RequestMapping(value = "/{collectionname}/getItem/{col}/{row}/previous", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollageItem> getItemPrev(@PathVariable("collectionname") String collectionname, @PathVariable("col") int col, @PathVariable("row") int row) {
        return service.getItemPrev(collectionname, col, row);
    }

    @RequestMapping(value = "/{clientname}/getUuid/", method = RequestMethod.GET)
    public ResponseEntity<String> getUuidFromClientName(@PathVariable("clientname") String clientname) {
        return service.getUuidFromClientName(clientname);
    }

    @RequestMapping(value = "/{collectionname}/Banner.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBanner(@PathVariable("collectionname") String collectionname) {
        return service.getBanner(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/Logo.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getLogo(@PathVariable("collectionname") String collectionname) {
        return service.getLogo(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/HeaderImage.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getHeaderImage(@PathVariable("collectionname") String collectionname) {
        return service.getHeaderImage(collectionname);
    }

    @RequestMapping(value = "/{collectionname}/LiveBigImage.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> LiveBigImage(@PathVariable("collectionname") String collectionname) {
        return service.getLiveBigImage(collectionname);
    }
}
