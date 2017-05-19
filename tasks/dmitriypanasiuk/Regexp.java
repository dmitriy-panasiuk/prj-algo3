package dmitriypanasiuk;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Regexp {
    private static Map<Character, Integer> oPriorities = new HashMap<>();
    static {
        oPriorities.put('+', 1);
        oPriorities.put('-', 1);
        oPriorities.put('*', 2);
        oPriorities.put('/', 2);
    }

    public static String infixToPostfix(String expression) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (Character c : expression.toCharArray()) {
            if (c == '(') stack.push(c);
            else if (c == ')') {
                while (stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                stack.pop();
            }
            else if (oPriorities.keySet().contains(c)) {
                if (stack.isEmpty() || stack.peek() == '(' || oPriorities.get(c) > oPriorities.get(stack.peek())) {
                    stack.push(c);
                } else {
                    while (!stack.isEmpty() && stack.peek() != '(' && oPriorities.get(stack.peek()) >= oPriorities.get(c)) {
                        postfix.append(stack.pop());
                    }
                    stack.push(c);
                }

            }
            else postfix.append(c);
        }
        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }

        return postfix.toString();
    }

    public static void main(String[] args) {
        String expression = "(a*(b+c)+d)/2";

        System.out.println(infixToPostfix(expression));
    }
}
