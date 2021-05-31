package com.capstone.gogreen;

import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
//SpringRunner is an alias for the SpringJUnit4ClassRunner, which joins the JUnit testing library with the Spring TestContext Framework.
@SpringBootTest(classes = GogreenApplication.class)
//This annotation tells the framework which Java Class with a main method starts the application
@AutoConfigureMockMvc
//This is an annotation that can be applied to a test class to enable and configure auto-configuration of MockMvc
public class JobsIntegrationTests {

    @Autowired
    UserRepository usersDao;
    @Autowired
    JobRepository jobsDao;
    private HttpSession httpSession;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {

        User testUser = usersDao.findByUsername("testUser");

        // Creates the test user if not exists
        if (testUser == null) {
            User newUser = new User();
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("pass"));
            newUser.setEmail("testUser@codeup.com");
            usersDao.save(newUser);
        }

        // Throws a post request to /login and expect a redirection to the gogreen index page after being logged in
        httpSession = this.mvc.perform(post("/login").with(csrf())
                .param("username", "testUser")
                .param("password", "pass"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/dashboard"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void testLoginUser() throws Exception {
        // Makes a test if created user is able to login and redirects /dashboard
        this.mvc.perform(
                post("/login").with(csrf())
                        .session((MockHttpSession) httpSession)
                        // Add all the required parameters to your request like this
                        .param("username", "testUser")
                        .param("password", "pass")
        )
                .andExpect(status().is3xxRedirection());
    }

//    @Test
//    public void testCreateReview() throws Exception {
//        Job existingjob = jobsDao.getOne(16L);
//        // Makes a test if created user is able to login and redirects /dashboard
//        this.mvc.perform(
//                post("/reviews/"+ existingjob +"/create").with(csrf())
//                        .session((MockHttpSession) httpSession)
//                        // Add all the required parameters to your request like this
//                        .param("reviewTitle", "Test Job Title")
//                        .param("reviewBody", "Test Job Review")
//        )
//                .andExpect(status().is3xxRedirection());
//    }

}
