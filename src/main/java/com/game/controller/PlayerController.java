package com.game.controller;

import com.game.dto.PlayerDto;
import com.game.entity.*;
import com.game.exceptions.BadRequest;
import com.game.filter.Filter;
import com.game.filter.QueryOperator;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        List<Filter> filters = new ArrayList<>();
        if (name != null)
            filters.add(new Filter("name", QueryOperator.LIKE, name));
        if (title != null)
            filters.add(new Filter("title", QueryOperator.LIKE, title));
        if (race != null)
            filters.add(new Filter("race", QueryOperator.EQUALS, race.toString()));
        if (profession != null)
            filters.add(new Filter("profession", QueryOperator.EQUALS, profession.toString()));
        if (after != null)
            filters.add(new Filter("birthday", QueryOperator.AFTER_THAN, String.valueOf(after)));
        if (before != null)
            filters.add(new Filter("birthday", QueryOperator.BEFORE_THAN, String.valueOf(before)));
        if (banned != null)
            filters.add(new Filter("banned", QueryOperator.EQUALS, banned.toString()));
        if (minExperience != null)
            filters.add(new Filter("experience", QueryOperator.GREATER_THAN, minExperience.toString()));
        if (maxExperience != null)
            filters.add(new Filter("experience", QueryOperator.LESS_THAN, maxExperience.toString()));
        if (minLevel != null)
            filters.add(new Filter("level", QueryOperator.GREATER_THAN, minLevel.toString()));
        if (maxLevel != null)
            filters.add(new Filter("level", QueryOperator.LESS_THAN, maxLevel.toString()));

        Page<Player> player = service.getAllPlayers(filters, pageNumber, pageSize, order);

        return player.getContent();
    }

    @GetMapping({"/count"})
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayersCount(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {

        List<Filter> filters = new ArrayList<>();
        if (name != null)
            filters.add(new Filter("name", QueryOperator.LIKE, name));
        if (title != null)
            filters.add(new Filter("title", QueryOperator.LIKE, title));
        if (race != null)
            filters.add(new Filter("race", QueryOperator.EQUALS, race.toString()));
        if (profession != null)
            filters.add(new Filter("profession", QueryOperator.EQUALS, profession.toString()));
        if (after != null)
            filters.add(new Filter("birthday", QueryOperator.AFTER_THAN, String.valueOf(after)));
        if (before != null)
            filters.add(new Filter("birthday", QueryOperator.BEFORE_THAN, String.valueOf(before)));
        if (banned != null)
            filters.add(new Filter("banned", QueryOperator.EQUALS, banned.toString()));
        if (minExperience != null)
            filters.add(new Filter("experience", QueryOperator.GREATER_THAN, minExperience.toString()));
        if (maxExperience != null)
            filters.add(new Filter("experience", QueryOperator.LESS_THAN, maxExperience.toString()));
        if (minLevel != null)
            filters.add(new Filter("level", QueryOperator.GREATER_THAN, minLevel.toString()));
        if (maxLevel != null)
            filters.add(new Filter("level", QueryOperator.LESS_THAN, maxLevel.toString()));

        return service.getCountPlayers(filters);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody PlayerDto playerDto) {
        if (playerDto.getName() == null || playerDto.getTitle() == null || playerDto.getRace() == null ||
                playerDto.getBirthday() == null || playerDto.getExperience() == null ||
                playerDto.getProfession() == null)
            throw new BadRequest();

        if (playerDto.getName().length() > 12 || playerDto.getTitle().length() > 30 || playerDto.getName().isEmpty() ||
                playerDto.getBirthday() < 0 || playerDto.getExperience() < 0 || playerDto.getExperience() > 10_000_000 ||
                playerDto.getBirthday() < new Date(2000 - 1900, Calendar.JANUARY, 1).getTime() ||
                playerDto.getBirthday() > new Date(3000 - 1900, Calendar.DECEMBER, 31).getTime())
            throw new BadRequest();

        return service.createPlayer(playerDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player findById(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequest();
        }

        return service.getPlayerById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player updatePlayer(@RequestBody PlayerDto playerDto, @PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequest();
        }

        if (playerDto.getName() != null && playerDto.getName().length() > 12 ||
                playerDto.getTitle() != null && playerDto.getTitle().length() > 30 ||
                playerDto.getBirthday() != null &&
                        (playerDto.getBirthday() < new Date(2000 - 1900, Calendar.JANUARY, 1).getTime() ||
                                playerDto.getBirthday() > new Date(3000 - 1900, Calendar.DECEMBER, 31).getTime()) ||
                playerDto.getExperience() != null && (playerDto.getExperience() > 10_000_000 || playerDto.getExperience() < 0)) {
            throw new BadRequest();
        }

        return service.updatePlayer(id, playerDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequest();
        }

        service.deletePlayerById(id);
    }
}
