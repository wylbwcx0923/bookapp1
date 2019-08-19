package com.jxtc.bookapp;

import java.util.Scanner;

public class Test {
    /**
     * 用户输入一个简单的运算式,如"1+1",最终就输出"1+1=2"
     *
     * @param args
     */
    public static void main(String[] args) {

        System.out.print("请您输入一个算术式:");
        String formula = new Scanner(System.in).next();
        int result = compute(formula);
        System.out.println("您输入的算术式的最终结果为:" + formula + "=" + result);
    }

    /**
     * 该方法用于计算用户输入的运算式
     *
     * @param formula 输入的运算式
     * @return 得出的结果
     */
    private static int compute(String formula) {
        //正则表达式匹配所有的数字
        String regix = "[0-9]";
        //将所有数字替换成"",得到的就是运算符
        String operator = formula.replaceAll(regix, "");
        //定义一个数组,获得数字
        String[] formulaAry = null;
        //定义一个变量保存结果
        int result = 0;
        //判断是加减乘除
        switch (operator) {
            case "+":
                formulaAry = formula.split("\\+");
                result = Integer.valueOf(formulaAry[0]) + Integer.valueOf(formulaAry[1]);
                break;
            case "-":
                formulaAry = formula.split("\\-");
                result = Integer.valueOf(formulaAry[0]) - Integer.valueOf(formulaAry[1]);
                break;
            case "*":
                formulaAry = formula.split("\\*");
                result = Integer.valueOf(formulaAry[0]) * Integer.valueOf(formulaAry[1]);
                break;
            case "/":
                formulaAry = formula.split("\\/");
                result = Integer.valueOf(formulaAry[0]) / Integer.valueOf(formulaAry[1]);
                break;
        }
        return result;
    }
}
