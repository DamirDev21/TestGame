package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.dto.PlayerDto;
import com.game.entity.*;
import com.game.exceptions.BadRequest;
import com.game.exceptions.NotFoundExceptions;
import com.game.filter.Filter;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Page<Player> getAllPlayers(List<Filter> filters, Integer pageNumber, Integer pageSize, PlayerOrder playerOrder) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(playerOrder.getFieldName()));
        Specification<Player> allSpecification = getAllSpecification(filters);
        return playerRepository.findAll(allSpecification, pageRequest);
    }

    @Override
    public Integer getCountPlayers(List<Filter> filters) {
        if (!filters.isEmpty()) {
            Specification<Player> allSpecification = getAllSpecification(filters);
            return (int) playerRepository.count(allSpecification);
        } else {
            return (int) playerRepository.count();
        }
    }

    private Specification<Player> getAllSpecification(List<Filter> filters) {
        Specification<Player> specification = Specification.where(createSpecification(filters.remove(0)));
        for (Filter filter : filters) {
            specification = specification.and(createSpecification(filter));
        }
        return specification;
    }

    private Specification<Player> createSpecification(Filter filter) {
        String field = filter.getField();
        String value = filter.getValue();

        switch (filter.getOperator()) {
            case LIKE:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get(field), "%" + value + "%");
            case EQUALS:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(field), castToRequiredType(root.get(field).getJavaType(), value));
            case GREATER_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.gt(root.get(field), (Number) castToRequiredType(root.get(field).getJavaType(), value));
            case LESS_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lt(root.get(field), (Number) castToRequiredType(root.get(field).getJavaType(), value));
            case AFTER_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get(field), (Date) castToRequiredType(root.get(field).getJavaType(), value));
            case BEFORE_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThan(root.get(field), (Date) castToRequiredType(root.get(field).getJavaType(), value));
            default:
                throw new BadRequest();
        }
    }

    private Object castToRequiredType(Class type, String value) {
        if (type.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (type.isAssignableFrom(Date.class)) {
            return new Date(Long.parseLong(value));
        } else if (type.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf(type, value);
        }
        return null;
    }

    @Override
    public Player createPlayer(PlayerDto playerDto) {
        Player player = new Player();
        int exp = playerDto.getExperience();

        player.setName(playerDto.getName());
        player.setTitle(playerDto.getTitle());
        player.setRace(playerDto.getRace());
        player.setProfession(playerDto.getProfession());
        player.setBirthday(new Date(playerDto.getBirthday()));
        player.setBanned(playerDto.getBanned() != null && playerDto.getBanned());
        player.setExperience(exp);
        player.setLevel(getLvl(exp));
        player.setUntilNextLevel(getUntilNextLevel(exp, getLvl(exp)));
        return playerRepository.save(player);
    }

    @Override
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElseThrow(NotFoundExceptions::new);
    }

    @Override
    public Player updatePlayer(Long id, PlayerDto playerDto) {
//        Player player = playerRepository.findById(id).orElseThrow(NotFoundExceptions::new);
        Player player;
        if (playerRepository.findById(id).isPresent()) {
            player = playerRepository.findById(id).get();
        } else {
            throw new NotFoundExceptions();
        }

        if (playerDto.getName() != null) player.setName(playerDto.getName());

        if (playerDto.getTitle() != null) player.setTitle(playerDto.getTitle());

        if (playerDto.getRace() != null) player.setRace(playerDto.getRace());

        if (playerDto.getProfession() != null) player.setProfession(playerDto.getProfession());

        if (playerDto.getBirthday() != null) player.setBirthday(new Date(playerDto.getBirthday()));

        if (playerDto.getBanned() != null) player.setBanned(playerDto.getBanned() != null && playerDto.getBanned());

        if (playerDto.getExperience() != null) {
            int exp = playerDto.getExperience();
            player.setExperience(exp);
            player.setLevel(getLvl(exp));
            player.setUntilNextLevel(getUntilNextLevel(exp, getLvl(exp)));
        }

        return playerRepository.saveAndFlush(player);
    }

    @Override
    public void deletePlayerById(Long id) {
        playerRepository.findById(id).orElseThrow(NotFoundExceptions::new);
        playerRepository.deleteById(id);
    }

    private Integer getUntilNextLevel(int exp, int lvl) {
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }

    private int getLvl(int exp) {
        return (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
    }
}




















