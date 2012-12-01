pointcut mainMethod() : execution(public static void main(String[]));


after() returning : mainMethod() {
    Systkem.out.println("Hello from AspectJ");
  }