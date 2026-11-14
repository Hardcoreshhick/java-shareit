package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    @Transactional
    public ItemRequestDto createRequest(Long userId, NewItemRequestDto dto) {
        log.debug("Создание запроса от пользователя id={}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = mapper.toEntity(dto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("Запрос создан: id={}", saved.getId());

        return mapper.toDto(saved, List.of());
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        log.debug("Получение своих запросов пользователя id={}", userId);

        checkUserExists(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return toDtoList(requests);
    }

    public List<ItemRequestDto> getOtherRequests(Long userId, Integer from, Integer size) {
        log.debug("Получение чужих запросов для пользователя id={}, from={}, size={}", userId, from, size);

        checkUserExists(userId);

        Pageable pageable = PageRequest.of(0, from + size);
        List<ItemRequest> page = requestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable)
                .getContent();
        List<ItemRequest> sliced = page.stream().skip(from).toList();

        return toDtoList(sliced);
    }

    public ItemRequestDto getRequestById(Long requestId) {
        log.debug("Получение запроса id={}", requestId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return mapper.toDto(request, items);
    }


    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private List<ItemRequestDto> toDtoList(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<Item>> itemsMap = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> mapper.toDto(
                        request,
                        itemsMap.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }
}
