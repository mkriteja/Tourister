package com.example.user.tourister;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieData implements Serializable {

    List<Map<String,?>> moviesList;

    public List<Map<String, ?>> getMoviesList() {
        return moviesList;
    }

    public int getSize(){
        return moviesList.size();
    }

    public HashMap getItem(int i){
        if (i >=0 && i < moviesList.size()){
            return (HashMap) moviesList.get(i);
        } else return null;
    }

    public MovieData(){
        String description;
		String length;
		String year;
		double rating;
		String director;
		String stars;
		String url;
        moviesList = new ArrayList<Map<String,?>>();
        //#1-10
		year = "2009";
		length = "162 min";
		rating = 7.9;
		director = "Cameron" ;
		stars = "Sam Worthington, Zoe Saldana, Sigourney Weaver";
		url ="http://ia.media-imdb.com/images/M/MV5BMTYwOTEwNjAzMl5BMl5BanBnXkFtZTcwODc5MTUwMw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
        description = "A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.";
		moviesList.add(createMovie("Avatar", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1997";
		length = "194 min";
		rating = 7.7;
		director = "James Cameron" ;
		stars = "Leonardo DiCaprio, Kate Winslet, Billy Zane";
		url ="http://ia.media-imdb.com/images/M/MV5BMjExNzM0NDM0N15BMl5BanBnXkFtZTcwMzkxOTUwNw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
        description = "Seventeen-year-old aristocrat, expecting to be married to a rich claimant by her mother, falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.";
		moviesList.add(createMovie("Titanic", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2012";
		length = "143 min";
		rating = 8.2;
		director = "Joss Whedon" ;
		stars = "Robert Downey Jr., Chris Evans, Scarlett Johansson";
		url ="http://ia.media-imdb.com/images/M/MV5BMTk2NTI1MTU4N15BMl5BanBnXkFtZTcwODg0OTY0Nw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "Nick Fury of S.H.I.E.L.D. assembles a team of superheroes to save the Movie from Loki and his army.";
        moviesList.add(createMovie("The Avengers", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2008";
		length = "152 min";
		rating = 9.0;
		director = "Christopher Nolan" ;
		stars = "Christian Bale, Heath Ledger, Aaron Eckhart";
		url = "http://ia.media-imdb.com/images/M/MV5BMTk4ODQzNDY3Ml5BMl5BanBnXkFtZTcwODA0NTM4Nw@@._V1_SX214_AL_.jpg";
		description = "When Batman, Gordon and Harvey Dent launch an assault on the mob, they let the clown out of the box, the Joker, bent on turning Gotham on itself and bringing any heroes down to his level.";
        moviesList.add(createMovie("The Dark Knight", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1999";
		length = "136 min";
		rating = 6.6;
		director = "George Lucas" ;
		stars = "Ewan McGregor, Liam Neeson, Natalie Portman";
		url ="http://ia.media-imdb.com/images/M/MV5BMTQ4NjEwNDA2Nl5BMl5BanBnXkFtZTcwNDUyNDQzNw@@._V1_SX214_AL_.jpg";
		description = "Two Jedi Knights escape a hostile blockade to find allies and come across a young boy who may bring balance to the Force, but the long dormant Sith resurface to reclaim their old glory.";
        moviesList.add(createMovie("Star Wars I", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1977";
		length = "121 min";
		rating = 8.7;
		director = "George Lucas" ;
		stars = "Mark Hamill, Harrison Ford, Carrie Fisher";
		url ="http://ia.media-imdb.com/images/M/MV5BMTU4NTczODkwM15BMl5BanBnXkFtZTcwMzEyMTIyMw@@._V1_SX214_AL_.jpg";
		description = "Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a wookiee and two droids to save the universe from the Empire's world-destroying battle-station, while also attempting to rescue Princess Leia from the evil Darth Vader.";
        moviesList.add(createMovie("Star Wars IV ", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2012";
		length = "165 min";
		rating = 8.6;
		director = "Christopher Nolan" ;
		stars = "Christian Bale, Tom Hardy, Anne Hathaway";
		url ="http://ia.media-imdb.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "Eight years after the Joker's reign of anarchy, the Dark Knight must return to defend Gotham City against the enigmatic jewel thief Catwoman and the ruthless mercenary Bane as the city teeters on the brink of complete annihilation.";
        moviesList.add(createMovie("The Dark Knight Rises", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2004";
		length = "93 min";
		rating = 7.3;
		director = "Andrew Adamson, Kelly Asbury" ;
		stars = "Mike Myers, Eddie Murphy, Cameron Diaz";
		url ="http://ia.media-imdb.com/images/M/MV5BMTk4MTMwNjI4M15BMl5BanBnXkFtZTcwMjExMzUyMQ@@._V1_SX214_AL_.jpg";
		description = "Princess Fiona's parents invite her and Shrek to dinner to celebrate her marriage. If only they knew the newlyweds were both ogres.";
        moviesList.add(createMovie("Shrek 2", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2009";
		length = "162 min";
		rating = 7.9;
		director = "Cameron" ;
		stars = "Sam Worthington, Zoe Saldana, Sigourney Weaver";
		url ="http://ia.media-imdb.com/images/M/MV5BMTYwOTEwNjAzMl5BMl5BanBnXkFtZTcwODc5MTUwMw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.";
		moviesList.add(createMovie("Avatar", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1997";
		length = "194 min";
		rating = 7.7;
		director = "James Cameron" ;
		stars = "Leonardo DiCaprio, Kate Winslet, Billy Zane";
		url ="http://ia.media-imdb.com/images/M/MV5BMjExNzM0NDM0N15BMl5BanBnXkFtZTcwMzkxOTUwNw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "Seventeen-year-old aristocrat, expecting to be married to a rich claimant by her mother, falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.";
		moviesList.add(createMovie("Titanic", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2012";
		length = "143 min";
		rating = 8.2;
		director = "Joss Whedon" ;
		stars = "Robert Downey Jr., Chris Evans, Scarlett Johansson";
		url ="http://ia.media-imdb.com/images/M/MV5BMTk2NTI1MTU4N15BMl5BanBnXkFtZTcwODg0OTY0Nw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "Nick Fury of S.H.I.E.L.D. assembles a team of superheroes to save the Movie from Loki and his army.";
		moviesList.add(createMovie("The Avengers", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2008";
		length = "152 min";
		rating = 9.0;
		director = "Christopher Nolan" ;
		stars = "Christian Bale, Heath Ledger, Aaron Eckhart";
		url = "http://ia.media-imdb.com/images/M/MV5BMTk4ODQzNDY3Ml5BMl5BanBnXkFtZTcwODA0NTM4Nw@@._V1_SX214_AL_.jpg";
		description = "When Batman, Gordon and Harvey Dent launch an assault on the mob, they let the clown out of the box, the Joker, bent on turning Gotham on itself and bringing any heroes down to his level.";
		moviesList.add(createMovie("The Dark Knight", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1999";
		length = "136 min";
		rating = 6.6;
		director = "George Lucas" ;
		stars = "Ewan McGregor, Liam Neeson, Natalie Portman";
		url ="http://ia.media-imdb.com/images/M/MV5BMTQ4NjEwNDA2Nl5BMl5BanBnXkFtZTcwNDUyNDQzNw@@._V1_SX214_AL_.jpg";
		description = "Two Jedi Knights escape a hostile blockade to find allies and come across a young boy who may bring balance to the Force, but the long dormant Sith resurface to reclaim their old glory.";
		moviesList.add(createMovie("Star Wars I", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "1977";
		length = "121 min";
		rating = 8.7;
		director = "George Lucas" ;
		stars = "Mark Hamill, Harrison Ford, Carrie Fisher";
		url ="http://ia.media-imdb.com/images/M/MV5BMTU4NTczODkwM15BMl5BanBnXkFtZTcwMzEyMTIyMw@@._V1_SX214_AL_.jpg";
		description = "Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a wookiee and two droids to save the universe from the Empire's world-destroying battle-station, while also attempting to rescue Princess Leia from the evil Darth Vader.";
		moviesList.add(createMovie("Star Wars IV ", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2012";
		length = "165 min";
		rating = 8.6;
		director = "Christopher Nolan" ;
		stars = "Christian Bale, Tom Hardy, Anne Hathaway";
		url ="http://ia.media-imdb.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SY317_CR0,0,214,317_AL_.jpg";
		description = "Eight years after the Joker's reign of anarchy, the Dark Knight must return to defend Gotham City against the enigmatic jewel thief Catwoman and the ruthless mercenary Bane as the city teeters on the brink of complete annihilation.";
		moviesList.add(createMovie("The Dark Knight Rises", R.drawable.welcome_image, description, year, length, rating, director, stars, url));
		year = "2004";
		length = "93 min";
		rating = 7.3;
		director = "Andrew Adamson, Kelly Asbury" ;
		stars = "Mike Myers, Eddie Murphy, Cameron Diaz";
		url ="http://ia.media-imdb.com/images/M/MV5BMTk4MTMwNjI4M15BMl5BanBnXkFtZTcwMjExMzUyMQ@@._V1_SX214_AL_.jpg";
		description = "Princess Fiona's parents invite her and Shrek to dinner to celebrate her marriage. If only they knew the newlyweds were both ogres.";
		moviesList.add(createMovie("Shrek 2", R.drawable.welcome_image, description, year, length, rating, director, stars, url));

    }


    private HashMap createMovie(String name, int image, String description, String year,
                                String length, double rating, String director, String stars, String url) {
        HashMap movie = new HashMap();
        movie.put("image",image);
        movie.put("name", name);
        movie.put("description", description);
		movie.put("year", year);
		movie.put("length",length);
		movie.put("rating",rating);
		movie.put("director",director);
		movie.put("stars",stars);
		movie.put("url",url);
		movie.put("selection",false);
        return movie;
    }
}
