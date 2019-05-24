import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class FetchOldGamesList {
    private static final String userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";
    static String[] Category={"action-adventure","adventure","amiga",
            "arcade-action","board","educational",
            "non-english","puzzle","role-playing",
            "simulation","sports","strategy","utility"};
    static int[] cns={6,36,38,96,6,21,4,21,18,31,30,47,2};

    private static class Game{
        String URL;
        int year;
        String name;
        String category;

        public Game() {
        }
    }

    public static void main(String[] args) {
        List<Game> gameList=new ArrayList<>();
        for (int i = 0; i < Category.length; i++) {
            for (int j = 1; j <=cns[i] ; j++) {
                String uTo="http://www.old-games.com/games/"+Category[i];
                if(j!=1)
                uTo=uTo+"/"+j;
                try{
                    Thread.sleep(1000);
                    Element document= Jsoup.connect(uTo).userAgent(userAgent).get().body();
                    Elements ps=document.getElementsByTag("p");
                    for(Element element:ps){
                        if(element.hasAttr("class"))continue;
                        Game game=new Game();
                        game.name=element.getElementsByTag("a").get(0).text();
                        game.category=Category[i];
                        game.year=Integer.parseInt(element.getElementsByTag("em").get(0).text());
                        game.URL="http://www.old-games.com/"+element.getElementsByTag("a").get(0).attr("href");
                        gameList.add(game);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Finished");
        List<Game> after=subList(gameList);
        System.out.println(true);
    }


    public static List<Game> subList(List<Game> gL){
        List<Game> lg=new ArrayList<>();
        for (Game game:gL)
            if(game.year>=2000 && game.year<=2003)
                lg.add(game);
        return lg;
    }
}
