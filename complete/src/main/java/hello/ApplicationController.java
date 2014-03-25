package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

	@Autowired
	PersonRepository repository;

	@RequestMapping("/index")
	public String index(Model model) {

		model.addAttribute("people", repository.findAll());
		return "index";
	}
}
