package com.megait.myhome.service;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Item;
import com.megait.myhome.domain.Member;
import com.megait.myhome.repository.AlbumRepository;
import com.megait.myhome.repository.BookRepository;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final AlbumRepository albumRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;


    public List<Album> getAlbumList(){
        return albumRepository.findAll();
    }

    public List<Book> getBookList(){
        return bookRepository.findAll();
    }




    @PostConstruct
    public void initBookItems() throws IOException {
        Resource resource = new ClassPathResource("book.CSV");
        List<Item> bookList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .stream()
                .map(line->{  // line : 각 줄
                    String[] split = line.split("\\|"); // '|' 단위로 단어 자르기
                    Book book = new Book();
                    book.setName(split[0]);
                    book.setImageUrl(split[1]);
                    book.setPrice(Integer.parseInt(split[2]));
                    return book;

                })  // 각 줄마다 무슨 일을 할 지?
                .collect(Collectors.toList()); // 리턴 받은 book들을 리스트에 담는다.
        itemRepository.saveAll(bookList);
    }

    @PostConstruct
    public void initAlbumItems() throws IOException {
        Resource resource = new ClassPathResource("album.CSV");
        List<Item> albumList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .stream()
                .map(line->{  // line : 각 줄
                    String[] split = line.split("\\|"); // '|' 단위로 단어 자르기
                    Album album = new Album();
                    album.setName(split[0]);
                    album.setImageUrl(split[1]);
                    album.setPrice(Integer.parseInt(split[2]));
                    return album;
                })  // 각 줄마다 무슨 일을 할 지?
                .collect(Collectors.toList()); // 리턴 받은 book들을 리스트에 담는다.
        itemRepository.saveAll(albumList);
    }

    public Item getItem(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if(item.isEmpty()){
            return null;
        }
        return item.get();
    }


    @Transactional
    public void addLikes(Member member, Long id) {

        if(member == null){
            throw new IllegalStateException("로그인이 필요한 기능입니다.");
        }

        member = memberRepository.findByEmail(member.getEmail());

        Optional<Item> itemOptional = itemRepository.findById(id);

        if(itemOptional.isEmpty()){
            throw new IllegalStateException("미등록 상품입니다.");
        }

        Item item = itemOptional.get();

        List<Item> list = member.getLikes();

        if(list.contains(item)){
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }


        item.setLiked( item.getLiked() + 1 );

        member.getLikes().add(item);
    }

    public void deleteLikes(Member member, List<Long> idList) {
        // idList : 찜목록 페이지에서 체크박스로 선택된 상품들의 id
        member = memberRepository.getOne(member.getId());
        member.getLikes().removeAll(itemRepository.findAllById(idList));
    }
}
