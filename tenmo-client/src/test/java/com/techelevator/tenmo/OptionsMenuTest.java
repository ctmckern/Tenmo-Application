package com.techelevator.tenmo;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class OptionsMenuTest {

    private ByteArrayOutputStream output;

    @Before
    public void setup() {
        output = new ByteArrayOutputStream();
    }
    //displays menu prompting user to select
    @Test
    public void displays_menu_options_and_prompts_user_for_choice() {
        Object[] options = new Object[] { Integer.valueOf(3), "Blind", "Mice" };

        com.techelevator.tenmo.view.ConsoleService console = getServiceForTesting();

        console.getChoiceFromOptions(options);

        String expected = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
                + options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> " + System.lineSeparator();
        Assert.assertEquals(expected, output.toString());
    }
    //if user types anything other than what is listed in the menu, it will just reload the menu for them to choose correctly
    @Test
    public void if_user_does_not_choose_valid_menu_option() {
        Object[] options = new Object[] { "Larry", "Curly", "Moe" };
        com.techelevator.tenmo.view.ConsoleService console = getServiceForTestingWithUserInput("4" + System.lineSeparator() + "1" + System.lineSeparator());

        console.getChoiceFromOptions(options);

        String menuDisplay = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
                + options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> ";

        String expected = menuDisplay + System.lineSeparator() + "*** 4 is not a valid option ***" + System.lineSeparator() + System.lineSeparator() + menuDisplay + System.lineSeparator();

        Assert.assertEquals(expected, output.toString());
    }
    //throws error if invalid integer is used for age
    @Test
    public void error_if_user_enters_invalid_integer() {
        com.techelevator.tenmo.view.ConsoleService console = getServiceForTestingWithUserInput("bogus" + System.lineSeparator() + "1" + System.lineSeparator());
        String prompt = "Your Age";
        String expected = "Your Age: " + System.lineSeparator() + "*** bogus is not valid ***" + System.lineSeparator() + System.lineSeparator() + "Your Age: ";
        console.getUserInputInteger(prompt);
        Assert.assertEquals(expected, output.toString());
    }
    @Test
    public void returns_user_input() {
        String expected = "Daniel";
        com.techelevator.tenmo.view.ConsoleService console = getServiceForTestingWithUserInput(expected);
        String result = console.getUserInput("Your Name");
        Assert.assertEquals(expected, result);
    }
    @Test
    public void displays_prompt_for_user_input() {
        com.techelevator.tenmo.view.ConsoleService console = getServiceForTesting();
        String prompt = "Your Name";
        String expected = "Your Name: ";
        console.getUserInput(prompt);
        Assert.assertEquals(expected, output.toString());
    }
    private com.techelevator.tenmo.view.ConsoleService getServiceForTesting() {
        return getServiceForTestingWithUserInput("1" + System.lineSeparator());
    }
    private  com.techelevator.tenmo.view.ConsoleService getServiceForTestingWithUserInput(String userInput) {
        ByteArrayInputStream input = new ByteArrayInputStream(String.valueOf(userInput).getBytes());
        return new  com.techelevator.tenmo.view.ConsoleService(input, output);
    }
}
