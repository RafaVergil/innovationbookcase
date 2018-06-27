package utils;


//I use this class to handle constants like server urls, timeouts, method names, file names, etc.
public class CONSTANTS {

    //requests
    public static final String urlBooksAPI = "https://www.googleapis.com/books/v1/volumes?q=%s&startIndex=%s&maxResults=%s&fields=%s";
    public static final String basicInfo = "items/id,items/volumeInfo/title,items/volumeInfo/subtitle,items/volumeInfo/authors,items/volumeInfo/publisher,items/volumeInfo/publishedDate,items/volumeInfo/description,items/volumeInfo/categories,items/volumeInfo/imageLinks/thumbnail,items/volumeInfo/previewLink,items/volumeInfo/infoLink";
}
