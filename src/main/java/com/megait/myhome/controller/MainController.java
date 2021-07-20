package com.megait.myhome.controller;

import com.google.gson.JsonObject;
import com.megait.myhome.domain.*;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.service.CurrentUser;
import com.megait.myhome.service.ItemService;
import com.megait.myhome.service.MemberService;
import com.megait.myhome.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

//    private final AlbumRepository albumRepository;
//    private final BookRepository bookRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ItemService itemService;

    private final MemberService memberService;

    private final OrderService orderService;

    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){

        List<Book> bookList = itemService.getBookList();
        List<Album> albumList = itemService.getAlbumList();

        model.addAttribute("bookList", bookList);
        model.addAttribute("albumList", albumList);

        if(member != null){
            model.addAttribute(member);
        }

        return "view/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/view/user/login";
    }

    @GetMapping("/store/detail")
    public String detail(Long id, Model model){
//        logger.info("id : " + id);
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);
        return "view/store/detail";
    }


    @GetMapping("/store/like")
//    @Transactional
    @ResponseBody  // 리턴값 (String)은 view 이름이 아니라 responseBody 부분이다!
    public String addLike(@CurrentUser Member member, Long id){

        JsonObject object = new JsonObject();

        // id : 찜 당할 상품의 id
        // member : 현재 로그인한 유저

        // Item : liked 1 증가
        // Member : likes 리스트에 해당 item 을 추가
        try {
            itemService.addLikes(member, id);
            object.addProperty("result", true);
            object.addProperty("message", "찜 목록에 등록되었습니다.");
        } catch (IllegalStateException e){
            object.addProperty("result", false);
            object.addProperty("message", e.getMessage());
        }
        logger.info("찜 결과 : " + object.toString());
        return object.toString();
    }

    @GetMapping("/store/like-list")
    public String likeList(@CurrentUser Member member, Model model){
        List<Item> likeList = memberService.getLikeList(member);

        model.addAttribute("likeList", likeList);

        return "view/store/like-list";
    }





    @PostMapping("/cart/list")  // 장바구니에 상품 추가한 뒤, 장바구니 DB 조회 후 뷰로 감.
    public String addCart(@CurrentUser Member member, @RequestParam("item_id") String[] itemId, Model model){
        // Order > Status.CART
        // OrderItem
        // Member > orders (List<Order>)



        // String[] ==> List<Long>
        List<Long> idList = List.of(Arrays.stream(itemId).map(Long::parseLong).toArray(Long[]::new));
        orderService.addCart(member, idList); // 1 ~ 4



        // 5. 찜목록에 있었던 상품 엔티티들을 삭제한다.
        itemService.deleteLikes(member, idList);

        // 장바구니 보기 페이지
        return cartList(member, model);
    }

    @GetMapping("/cart/list")  // 장바구니 DB 조회 후 뷰로 감.
    public String cartList(@CurrentUser Member member, Model model){
        try {
            // 현재 유저의 장바구니 받기
            List<OrderItem> cartList = orderService.getCart(member);
            model.addAttribute("cartList", cartList);
            model.addAttribute("totalPrice", orderService.getTotalPrice(cartList));
        } catch (IllegalStateException e){
            model.addAttribute("error_message", e.getMessage()); // empty.cart
        }
        return "view/cart/list";
    }

    @PostMapping("/cart/delete")
    public String cartDelete(@CurrentUser Member member,
                             @RequestParam(value = "item_id", required = false)String[] itemIds,
                             Model model){
        if(itemIds != null && itemIds.length != 0){
            List<Long> idList = List.of(Arrays.stream(itemIds).map(Long::parseLong).toArray(Long[]::new));
            orderService.deleteCart(member, idList);
        }
        return cartList(member, model);
    }
}
