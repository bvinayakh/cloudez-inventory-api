package com.cez.api.v1.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

@Component
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthExceptionResolver extends AbstractHandlerExceptionResolver
{
  @Override
  protected ModelAndView doResolveException(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
  {
    System.out.println(arg1.getStatus());
    System.out.println(arg3.getMessage());
    return null;
  }
}
