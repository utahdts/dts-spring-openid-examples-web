package gov.utah.dts.openid.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;


@ControllerAdvice
public class MyExceptionHandler {

	@ExceptionHandler(value = NullPointerException.class)
	public String nullPointerHandler(Model theModel) {
		theModel.addAttribute("err", "NullPointerException");
		return "error";
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handle404(HttpServletRequest request, Model theModel, NoHandlerFoundException e) {
		theModel.addAttribute("err", e.getMessage());
		return "error";
	}

	@ExceptionHandler(value = Exception.class)
	public String anyOtherHandler(Model theModel, Exception e) {
		// It's good practice to log the exception
		// log.error("Unhandled exception occurred", e);
		theModel.addAttribute("err", "An unexpected error occurred.");
		return "error";
	}

}
