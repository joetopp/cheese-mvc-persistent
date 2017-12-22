package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    private String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    private String add(Model model) {
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    private String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if(errors.hasErrors()) {
            return "menu/add";
        } else {
            menuDao.save(menu);
            return "redirect:view/" + menu.getId();
        }
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    private String viewMenu(Model model, @PathVariable int id) {

        model.addAttribute("menu", menuDao.findOne(id));

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    private String addItem(Model model, @PathVariable("id") int id) {
        Menu aMenu = menuDao.findOne(id);
        AddMenuItemForm addMenuItemForm= new AddMenuItemForm(aMenu, cheeseDao.findAll());

        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("title", "Add item to menu: " + aMenu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    private String addItem(Model model, @Valid AddMenuItemForm addMenuItemForm, Errors errors) {

        if (errors.hasErrors()) {
            return "add-item";
        } else {
            Cheese aCheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
            Menu aMenu = menuDao.findOne(addMenuItemForm.getMenuId());

            aMenu.addItem(aCheese);
            menuDao.save(aMenu);

            return "redirect:/menu/view/" + aMenu.getId();
        }
    }
}
