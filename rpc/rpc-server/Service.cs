using System.Reflection.Emit;
using System;

class Service{
    [Command("fib")]
    public static int Fib(int n){
        if(n<=1) return n;
        return Fib(n-2) + Fib(n-1);
    }

    [Command("sort")]
    public static string Sort(int[] values){
        if(values.Length == 0) return "";
        Array.Sort(values);
        return string.Join(" ", values);
    }
}
