package greenNare.place.controller;

import greenNare.Response.SingleResponseDto;
import greenNare.place.dto.PlaceDto;
import greenNare.place.entity.Place;
import greenNare.place.mapper.PlaceMapper;
import greenNare.place.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nare/map")
@Validated
@Slf4j
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceMapper mapper;

    public PlaceController(PlaceService placeService, PlaceMapper mapper) {
        this.placeService = placeService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createPlace(@RequestBody PlaceDto.Post placePostDto,
                                      @RequestHeader(value = "Authorization", required = false) String token){
        Place createdPlace = placeService.createPlace(mapper.placePostDtoToPlace(placePostDto), token);

        return new ResponseEntity<>(new SingleResponseDto<>(createdPlace), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getPlaces()  {
        List<Place> response = placeService.getPlaces();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity deletePlace(@PathVariable int placeId,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        placeService.deletePlace(placeId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
