package ua.kostenko.recollector.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling the root URL of the application.
 * Redirects to the index page.
 */
@Controller
@Slf4j
public class IndexController {

    /**
     * Handles requests to the root URL ("/").
     *
     * @return the name of the view to be rendered, in this case, "index".
     */
    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = "/")
    public String index() {
        log.info("Accessing the index page");
        return "index";
    }
}
