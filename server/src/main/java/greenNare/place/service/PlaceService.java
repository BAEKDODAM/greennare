package greenNare.place.service;

import greenNare.auth.jwt.JwtTokenizer;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.entity.Member;
import greenNare.member.service.MemberService;
import greenNare.place.entity.Place;
import greenNare.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;

    public PlaceService(PlaceRepository placeRepository, JwtTokenizer jwtTokenizer, MemberService memberService) {
        this.placeRepository = placeRepository;
        this.jwtTokenizer = jwtTokenizer;
        this.memberService = memberService;
    }

    public Place createPlace(Place place, String token) {
        Member member = findMemberByToken(token);
        verifyExistsPlace(place.getLat(), place.getLongi());
        place.setMember(member);
        return placeRepository.save(place);
    }

    public List<Place> getPlaces() {
        return placeRepository.findAll();
    }

    public void deletePlace(int placeId, String token) {
        int memberId = findMemberIdByToken(token);
        Place findPlace = findPlaceById(placeId);
        validateWriter(memberId, placeId);
        placeRepository.delete(findPlace);
    }
    public Place findPlaceById(int placeId) {
        Optional<Place> findPlace = placeRepository.findById(placeId);
        Place place = findPlace.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.PLACE_NOT_FOUND));
        return place;
    }

    public int findMemberIdByToken(String token) {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        return  jwtTokenizer.getMemberId(token);
    }
    public Member findMemberByToken(String token) {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        int memberId = jwtTokenizer.getMemberId(token);
        return memberService.findMemberById(memberId);
    }

    public void verifyExistsPlace(double lat, double longi) {
        boolean exist = placeRepository.findByLatAndLongi(lat, longi).isPresent();
        if (exist) throw new BusinessLogicException(ExceptionCode.PLACE_EXIST);
    }

    public void validateWriter(int memberId, int placeId) {
        Optional<Place> place = placeRepository.findById(placeId);
        if(memberId != place.get().getMember().getMemberId()){
            throw new BusinessLogicException(ExceptionCode.UNMATCHED_WRITER);
        }
    }
}
