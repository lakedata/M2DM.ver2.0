package ddwu.project.mdm_ver2.domain.mypage.controller;

import ddwu.project.mdm_ver2.domain.favorite.entity.Favorite;
import ddwu.project.mdm_ver2.domain.favorite.service.FavoriteService;
import ddwu.project.mdm_ver2.domain.grouppurchase.entity.GroupPurchase;
import ddwu.project.mdm_ver2.domain.grouppurchase.service.GroupPurchaseService;
import ddwu.project.mdm_ver2.domain.mypage.dto.AddressRequest;
import ddwu.project.mdm_ver2.domain.mypage.service.MyPageService;
import ddwu.project.mdm_ver2.domain.review.entity.Review;
import ddwu.project.mdm_ver2.domain.review.service.ReviewService;
import ddwu.project.mdm_ver2.domain.user.entity.User;
import ddwu.project.mdm_ver2.domain.user.repository.UserRepository;
import ddwu.project.mdm_ver2.global.exception.CustomResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/mypage")
public class MyPageController implements MyPageApi {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final MyPageService myPageService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final GroupPurchaseService groupPurchaseService;

    /* 닉네임 중복 확인 */
    @GetMapping("/check/{nickname}")
    public boolean checkNickname(@PathVariable(value="userNickname", required=true) String nickname, Model model) {
        model.addAttribute("nicknameDup", myPageService.checkNicknameDup(nickname)); // 중복 -> true, 중복X -> false
        return myPageService.checkNicknameDup(nickname);
    }

    /* 닉네임 변경 */
    @PostMapping("/nickname/{nickname}")
    public CustomResponse<User> setUserNickname(Principal principal, @PathVariable(value="userNickname", required=true) String nickname) {
        return myPageService.setUserNickname(principal.getName(), nickname);
    }

    /* 주소 설정, 변경 */
    @PostMapping("/address")
    public CustomResponse<User> setUserAddr(Principal principal, @RequestBody AddressRequest request) {
        return myPageService.setUserAddress(principal.getName(), request);
    }

    /* 사용자 찜 리스트 */
    @GetMapping("/favorite")
    public CustomResponse<List<Favorite>> getUserFavorite(Principal principal) {
        return favoriteService.getUserFavoriteList(principal.getName());
    }

    /* 사용자 작성 리뷰 리스트 */
    @GetMapping("/review")
    public CustomResponse<List<Review>> getUserReview(Principal principal) {
        return reviewService.getUserReviewList(principal.getName());
    }

    /* 사용자 참여 공동구매 상품 리스트 */
    @GetMapping("/gp")
    public CustomResponse<List<GroupPurchase>> getGroupPurchasesByUser (Principal principal){
        return groupPurchaseService.getGroupPurchasesByUser(principal.getName());
    }
}