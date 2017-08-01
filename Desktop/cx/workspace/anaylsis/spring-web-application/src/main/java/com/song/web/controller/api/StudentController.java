package com.song.web.controller.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.song.data.model.Course;
import com.song.data.model.Student;

@Controller
public class StudentController {
	
	@ResponseBody
	@RequestMapping(value = "/getstudent/{id}/{name}",
		method = RequestMethod.GET,
		produces={"application/json", "application/xml"})
	public Student getStudent(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") final int id,
			@PathVariable("name") final String name) {
		
		// Create a new student object and return it
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		Student student = new Student(id, name, dateFormatter.format(new Date()));
		
		List<Course> cources = student.getCourses();
		cources.add(new Course("Math", 15));
		cources.add(new Course("Politics", 100));
		
		return student;
	}
	
	@ResponseBody
	@RequestMapping(value = "/echostudent",
		method = RequestMethod.POST,
		produces={"application/json", "application/xml"},
		consumes={"application/json", "application/xml"})
	public Student echoStudent(@RequestBody Student student,
			HttpServletRequest request, HttpServletResponse response) {
		
		// Just echo the same student bac
		return student;
	}
	
	@RequestMapping(
	    value = "/get/{id}/{name}.*",
	    method = RequestMethod.GET,
	    produces={"application/json", "application/xml"})
	@ResponseBody
	public Student getDetails(@PathVariable int id,
			@PathVariable("name") final String name) {
		return getStudent(id,name);
	}

	@RequestMapping(
	    value = "/student.*",
	    method = RequestMethod.GET,
	    produces={"application/json", "application/xml"})
	@ResponseBody
	public Student getStudentByRequest(@ModelAttribute Student student) {
	    return student;
	}

	
	@RequestMapping(
	    value = "/get/{id}/{name}/json",
	    method = RequestMethod.GET,
	    produces = "application/json")
	@ResponseBody
	public Student getDetailsAsJson(
			@PathVariable int id,
			@PathVariable("name") final String name) {
	    return getStudent(id,name);
	}

	@RequestMapping(
	    value = "/get/{id}/{name}/xml",
	    method = RequestMethod.GET,
	    produces = "application/xml")
	@ResponseBody
	public Student getDetailsAsXml(
			@PathVariable int id,
			@PathVariable("name") final String name) {
	    return getStudent(id,name);
	}	
	
	public Student getStudent(int id, String name)
	{
		// Create a new student object and return it
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		Student student = new Student(id, name, dateFormatter.format(new Date()));
		
		List<Course> cources = student.getCourses();
		cources.add(new Course("Math", 15));
		cources.add(new Course("Politics", 100));
		
		return student;

	}
}
