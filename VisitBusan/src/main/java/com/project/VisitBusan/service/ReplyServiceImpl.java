package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.dto.ReplyDTO;
import com.project.VisitBusan.entity.Reply;
import com.project.VisitBusan.repository.BoardRepository;
import com.project.VisitBusan.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    // 1. 댓글 등록 구현
    @Override
    public Long register(ReplyDTO replyDTO) {

        Reply reply = dtoToEntity(replyDTO);

        Long id= replyRepository.save(reply).getId();

        return id;
    }
    // 2. 댓글 조회 구현
    @Override
    public ReplyDTO read(Long id) {
        Optional<Reply> replyOptonal = replyRepository.findById(id);
        Reply reply = replyOptonal.orElseThrow();

        return entityToDTO(reply);
    }
    // 3. 댓글 수정 구현
    @Override
    public void modify(ReplyDTO replyDTO) {
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getId());
        Reply reply = replyOptional.orElseThrow();

        reply.changeText(replyDTO.getReplyText());

        replyRepository.save(reply);
    }
    // 4. 댓글 삭제 구현
    @Override
    public void remove(Long id) {
        log.info("reply remove id:"+id);
        replyRepository.deleteById(id);
    }
    // 5. 댓글 목록
    @Override
    public PageResponseDTO<ReplyDTO> getListOBoard(Long board_id,
                                                   PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0: pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("id").ascending());
        Page<Reply> result = replyRepository.listOfBoard(board_id, pageable);

        List<ReplyDTO> dtoList =
                result.getContent()
                    .stream()
                    .map(reply -> entityToDTO(reply))
                    .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

}
