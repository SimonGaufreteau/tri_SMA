public class Main {
    public static void main(String[] args) {
        Runner runner = new Runner();
        try{
            runner.run(10000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
