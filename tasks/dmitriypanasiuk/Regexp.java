package dmitriypanasiuk;

import java.util.Stack;

public class Regexp {

    public static String infixToPostfix(String expression) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (Character c : expression.toCharArray()) {
            switch (c) {
                case '(':
                    stack.push(c);
                    break;
                default:
                    postfix.append(c);
            }
        }

        return expression;
    }

    public static void main(String[] args) {
        String expression = "(6+5*2)/4";

        System.out.println(infixToPostfix(expression));
    }
}
