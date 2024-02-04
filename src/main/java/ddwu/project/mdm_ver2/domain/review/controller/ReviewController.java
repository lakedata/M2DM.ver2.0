package ddwu.project.mdm_ver2.domain.review.controller;

import ddwu.project.mdm_ver2.domain.review.dto.ReviewRequest;
import ddwu.project.mdm_ver2.domain.review.entity.Review;
import ddwu.project.mdm_ver2.domain.review.service.ReviewService;
import ddwu.project.mdm_ver2.domain.secondhand.dto.SecondHandBidRequest;
import ddwu.project.mdm_ver2.global.exception.CustomResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/product/{prodId}/review")
public class ReviewController {

    private ReviewService reviewService;

    /* 리뷰 정렬 */
    @GetMapping("sort")
    public CustomResponse<List<Review>> getSortedList(@PathVariable("prodId") Long prodId,
                                                      @RequestParam(name = "sortBy", required = false, defaultValue = "") String sortBy) {
        return reviewService.sortReview(prodId, sortBy);
    }

    /* 리뷰 등록 */
    @PostMapping("/add")
    public CustomResponse<Review> addReview(@RequestParam("userEmail") String userEmail,
//            Principal principal,
                                            @PathVariable("prodId") Long prodId,
                                            @RequestBody ReviewRequest request) {
//        return reviewService.addReview(principal.getName(), prodId, request);
        return reviewService.addReview(userEmail, prodId, request);
    }

    /* 리뷰 수정 */
    @PostMapping("/update/{reviewId}")
    public CustomResponse<Review> updateReview(@RequestParam("userEmail") String userEmail,
//            Principal principal,
                                               @PathVariable("reviewId") Long reviewId,
                                               @RequestBody ReviewRequest request) {
//        return reviewService.updateReview(principal.getName(), reviewId, request);
        return reviewService.updateReview(userEmail, reviewId, request);
    }

    /* 리뷰 삭제 */
    @DeleteMapping("/delete/{reviewId}")
    public CustomResponse<Void> deleteReview(@RequestParam("userEmail") String userEmail,
//            Principal principal,
                                             @PathVariable("reviewId") Long reviewId) {
        return reviewService.deleteReview(userEmail, reviewId);
    }
}