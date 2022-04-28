package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.dto.PlayerDto;
import com.game.entity.Player;
import com.game.filter.Filter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    Page<Player> getAllPlayers(List<Filter> filters, Integer pageNumber, Integer pageSize, PlayerOrder playerOrder);

    Integer getCountPlayers(List<Filter> filters);

    Player createPlayer(PlayerDto playerDto);

    Player getPlayerById(Long id);

    Player updatePlayer(Long id, PlayerDto playerDto);

    void deletePlayerById(Long id);
}
