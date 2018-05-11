package com.graduation_project.android.algebrablade.utils;
import android.util.Log;

import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Variable;
import com.daoshengwanwu.math_util.calculator.VariableAssistant;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.List;

public class LeastSquareMethodFromApache {

    /*只提供离散点集*/
    public static String testLeastSquareMethodFromApache(ArrayList<Float> points ) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i=0;i<points.size();i=i+2){
            obs.add(points.get(i),points.get(i+1));
        }

        // Instantiate a second-degree polynomial fitter.默认拟合成二阶
         PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

        // Retrieve fitted parameters (coefficients of the polynomial function).
         double[] coeff = fitter.fit(obs.toList());
         int i=0;
         String str="";
        for (double c : coeff) {
            str+=c+"*x^"+i+"+";
            i++;
        }
        str=str.substring(0,str.length()-1);
        //System.out.println(str);
        return str.toLowerCase();
    }
    /*提供离散点集和阶数*/
    public static String testLeastSquareMethodFromApache(ArrayList<Float> points,int degree ) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i=0;i<points.size();i=i+2){
            obs.add(points.get(i),points.get(i+1));
        }


        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        double[] coeff = fitter.fit(obs.toList());
        int i=0;
        String str="";
        for (double c : coeff) {
            str+=c+"*x^"+i+"+";
            i++;
        }
        str=str.substring(0,str.length()-1);
        //System.out.println(str);
        return str.toLowerCase();
    }

    /*提供离散点集和拟合率*/
    public static String testLeastSquareMethodFromApache(ArrayList<Float> points,double probaility ) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i=0;i<points.size();i=i+2){
            obs.add(points.get(i),points.get(i+1));
        }
        boolean flag=true;
        int degree=0;
        String str="";
        while(flag) {
            str="";
            // Instantiate a third-degree polynomial fitter.
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
            // Retrieve fitted parameters (coefficients of the polynomial function).
            double[] coeff = fitter.fit(obs.toList());
            int i=0;
            for (double c : coeff) {
                str+=c+" * x^"+i+" + ";
                i++;
            }
            str=str.substring(0,str.length()-2);
            str = str.toLowerCase();
            Log.d("fitting", "testLeastSquareMethodFromApache: " + str);
            if(probilityMachedDegree(points,str)>probaility){
                flag=false;
            }else{
                degree++;
           }
        }

        return str.toLowerCase();
    }

    /*求拟合曲线拟合率*/
    public static double probilityMachedDegree(ArrayList<Float> points,String expStr){
        int success=0;
        int faulse=0;
        for(int i=0;i<=points.size()-2;i+=2){
            double xi=points.get(i);
            double yi=points.get(i+1);
            Calculator calculator = new Calculator();
            VariableAssistant assistant = new VariableAssistant().addVariable(
                    "x", -1, false, 1, false, 0.1
            );
            Variable x = assistant.getVariable("x");
            Calculator.ResultGenerator resultGenerator = calculator.calculate(new VarAriExp(expStr, assistant));

            x.setCurValue(xi);
            double y = resultGenerator.curValue();
            double k = Math.abs(maxY(points) - min(points)) / 20;
            if (k <= 0.000000001) {
                k = 0.000000001;
            }
            if(Math.abs(y-yi)<k){
                success++;
            }else{
                faulse++;
            }
        }
        return (double) success/(success+faulse);
    }

    /**
     * 例如当参数是PolynomialCurveFitter.create(1);
     * 点(-3, 4) (-2, 2) 此时拟合函数参数为-2，-2，公式为y=-2-2x;验证符合
     * @param args
     */

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        String expStr = "1+2*x+3*x^2";
        VariableAssistant assistant = new VariableAssistant().addVariable("x", -1, false, 1, false, 0.1);
        Variable x = assistant.getVariable("x");
        VarAriExp varAriExp = new VarAriExp(expStr, assistant);
        Calculator.ResultGenerator resultGenerator = calculator.calculate(varAriExp);

        ArrayList<Float> points = new ArrayList<>();
        float start = -100, end = 10000;
        for (float i = start; i <= end; i++) {
            points.add(i);
            x.setCurValue(i);
            points.add((float) resultGenerator.curValue());
        }
        String str=testLeastSquareMethodFromApache(points, 0.8);
        System.out.println(str);
    }


    private static float maxY(List<Float> points) {
        if (points.size() < 2) {
            return 0;
        }

        float max = points.get(1);
        for (int i = 3; i < points.size(); i += 2) {
            if (points.get(i) > max) {
                max = points.get(i);
            }
        }

        return max;
    }

    private static float min(List<Float> points) {
        if (points.size() < 2) {
            return 0;
        }

        float min = points.get(1);
        for (int i = 3; i < points.size(); i += 2) {
            if (points.get(i) < min) {
                min = points.get(i);
            }
        }

        return min;
    }
}
