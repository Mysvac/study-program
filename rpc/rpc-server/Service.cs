using System.Reflection.Emit;
using System;

class Service{
    public static int Fib(int n){
        if(n<=1) return n;
        return Fib(n-2) + Fib(n-1);
    }

    public static void Sort(int[] values){
        Array.Sort(values);
    }
}
