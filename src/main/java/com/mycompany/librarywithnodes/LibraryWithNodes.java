/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.librarywithnodes;

/**
 *
 * @author red_alien_13
 */
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Scanner;
import java.util.Stack;

public class LibraryWithNodes {
    
    public static void main(String[] args) {
        
        Node root = new LibraryWithNodes.Node("Library", "Root");
        LibraryView libraryView = new LibraryView(root);
    }

    static class Node {
        private String _val;
        private String _name;
        private String _relation;
        private int _depth = 0;
        private List<Node> Children = new ArrayList<>();
        private Consumer<Void> action;
        private String _description = "It seems that this does not have a description";
          

        public Node(String value, String name) {
            _name = name;
            _val = value;
        }

        public Node(String name, String value, String relation) {
            this(value, name);
            _relation = relation;
        }
        public void SetDescription(String str){
            if(str.length() == 0){
                return;
            }
            
            _description = str;
        }
        public void addNode(Node node) {
            Children.add(node);
        }

        public void removeNode(Node node) {
            if (!Children.remove(node)) {
                throw new IllegalArgumentException(String.format("This Child does not exist from Node: %s", _name));
            }
        }

        public void renameNode(String name) {
            _name = name;
        }

        public void updateNode(String value) {
            _val = value;
        }

        public void readChildren() {
            if (Children.isEmpty()) {
                System.out.println("No children to display.");
                return;
            }

            if (_relation != null && _name != null && !_name.trim().isEmpty()) {
                printIndented(String.format("%s's %ss:", _name, _relation), _depth - 1);
            }

            for (int i = 0; i < Children.size(); i++) {
                printIndented(String.format("[%d] %s", i + 1, Children.get(i)._name), _depth);
            }
        }

        public void setThisNodeAsChild(Node parent) {
            parent.addNode(this);
            parent.setChildDepth(this);
        }

        protected void setChildDepth(Node child) {
            child.setDepthFromParent(_depth + 1);
        }

        protected void setDepthFromParent(int depth) {
            _depth = depth;
            for (Node child : Children) {
                child.setDepthFromParent(depth + 1);
            }
        }

        private void printIndented(String message, int indentLevel) {
            for (int i = 0; i <= indentLevel; i++) {
                System.out.print('\t');
            }
            System.out.println(message);
        }

        public void setAction(Consumer<Void> action) {
            this.action = action;
        }

        public void executeAction() {
            if (action != null) {
                action.accept(null);
            }
        }

        public String getName() {
            return _name;
        }

        public String getValue() {
            return _val;
        }

        public List<Node> getChildren() {
            return Children;
        }

        public int getDepth() {
            return _depth;
        }
        public String getDescription(){
            return _description;
        }
    }

    static class LibraryView {
        private Node _curr;
        private final Stack<Node> navigationStack = new Stack<>();
        private boolean isRunning = true;
        private final Scanner scanner = new Scanner(System.in);

        public LibraryView(Node root) {
            _curr = root;
            System.out.println("Welcome to Library!!!");
            SetNodes();
        }

        private void SetNodes() {
            if (_curr == null) throw new NullPointerException("Root is null");

            Node homeNode = new Node("", "Home");
            Node AddNewBook = new Node("", "Add New Books");
            Node BrowseBooks = new Node("", "Browse Books");
            Node Exit = new Node("", "Exit");
            
            Node sub = new Node("", "Sub");
            SetCategories(sub);
            AddNewBook.addNode(sub);
            BrowseBooks.addNode(sub);
            SetHomeNode(homeNode, AddNewBook, BrowseBooks, Exit);
            homeNode.action.accept(null);
            
            
        }

        private void addBook() {
            
        }


        private void browseBooks() {
            }

        private void SetHomeNode(Node homeNode, Node AddNewBook, Node BrowseBooks, Node Exit) {
            homeNode.addNode(AddNewBook);
            homeNode.addNode(BrowseBooks);
            homeNode.addNode(Exit);
            _curr = homeNode;
            Consumer<Void> action = unused -> {
                
                System.out.println("Please select the corresponding actions");
                homeNode.readChildren();
                int choice = 0;
                System.out.print("Your Input: ");
                String input = scanner.nextLine();
                
                try{
                    choice = Integer.parseInt(input);
                    if(choice <= 0 || choice > homeNode.getChildren().size()){
                        throw new IllegalArgumentException("Invalid Choice. Please try again.");
                    }
                } catch (IllegalArgumentException e){
                    System.out.println(e.getMessage() + "\n");
                    homeNode.action.accept(unused);
                }
                
                switch (choice){
                    case 1 -> {
                        SetAddBooks(AddNewBook);
                        MoveForward(AddNewBook);
                    }
                    case 2 -> {
                        SetBrowseBooks(BrowseBooks);
                        MoveForward(BrowseBooks);
                    }
                    case 3 -> {
                        SetExit(Exit);
                        MoveForward(Exit); 
                    }
                    default -> {
                        throw new IllegalStateException("Impossible to happen"); //imagine if it did
                    }
                }
                
            };
            homeNode.setAction(action);
        }
        
        private void SetAddBooks(Node AddBooks){
            Node CategorySelection = AddBooks.getChildren().get(0);
            var categories = CategorySelection.getChildren();
//            Node SelectedSubCategory = null;
            
            Consumer<Void> action = unused -> {
                int choice = 0;
                System.out.println("Select Category for the new book");
                System.out.println("\t[0] Cancel and go back to Home");
                CategorySelection.readChildren();
                System.out.println("Your Input: ");
                String input = scanner.nextLine();
                
                try{
                    choice = Integer.parseInt(input);
                    if(choice < 0 || choice > categories.size()){
                        throw new IllegalArgumentException("Invalid Choice. Please try again");
                    }
                } catch (IllegalArgumentException e){
                    System.out.println(e.getMessage() + '\n');
                    AddBooks.action.accept(unused);
                }
                
                if(choice == 0){
//                    System.out.println("Weird Error here");
                    MoveBackward();
                }
                Node selected = categories.get(choice - 1);
                Consumer<Void> childAction = unusedsub -> {
                    int subChoice = 0;
                    System.out.println("\t[0] Cancel and go back to Home");
                    selected.readChildren();
                    System.out.println("Your Input: ");
                    String subinput = scanner.nextLine();
                    
                    try{
                        subChoice = Integer.parseInt(subinput);
                        if(subChoice < 0 || subChoice > categories.size()){
                        throw new IllegalArgumentException("Invalid Choice. Please try again");
                        }
                    }
                    catch (IllegalArgumentException ex)
                    {
                        System.out.println(ex.getMessage() + '\n');
                        selected.action.accept(unusedsub);
                    }
                    
                    if(subChoice == 0){
                        MoveBackward();
                    }
                    Node selectedsub = selected.getChildren().get(subChoice - 1);
                    Node newBook;
                    System.out.println("Enter Book Name: ");
                    String bookname = scanner.nextLine();
                    System.out.println("Enter Book Description");
                    String bookDesc = scanner.nextLine();
                    
                    newBook = new Node(selectedsub.getValue(), String.format("%s %s", selectedsub.getValue(), bookname));
                    newBook.SetDescription(bookDesc);
                    
                    System.out.println("\nNew Book Added!");
                    System.out.println("Book Name: " + newBook.getName());
                    System.out.println("Book Description: " + newBook.getDescription() + '\n');
                    

                    while(!navigationStack.peek().getName().equals("Home")){
                        navigationStack.pop();
                    }
                    navigationStack.peek().action.accept(null);
                    
                        
                  };
                selected.setAction(childAction);
                MoveForward(selected);
            };
        
            
            
            AddBooks.setAction(action);
//            MoveForward();
        }
        private void SetCategories(Node Parent)
        {
            Node genWorks = new Node("000", "000 Computer science, knowledge, and systems");
            Node philPsych = new Node("100", "100 Philosophy and Psychology");
            Node religion = new Node("200", "200 Religion");
            Node socScie = new Node("300", "300 Social Sciences");
            Node lang = new Node("400", "400 Language");
            Node science = new Node("500", "500 Science");
            Node tech = new Node("600", "600 Technology");
            Node arts = new Node("700", "700 Arts and Recreation");
            Node lit = new Node("800", "800 Literature");
            Node histoGeo = new Node("900", "900 History and Geography");
            
            //CLASS 000
            Node bibliography = new Node("010", "Bibliography");
            Node libraryAndInfoSciences = new Node("020", "Library and Information Sciences");
            Node genEncyclopedicWorks = new Node("030", "General encyclopedic works");
            genWorks.addNode(bibliography);
            genWorks.addNode(libraryAndInfoSciences);
            genWorks.addNode(genEncyclopedicWorks);
            
            Node programming = new Node("010.1", "Programming");
            Node networking = new Node("010.2", "Networking");
            genWorks.addNode(programming);
            genWorks.addNode(networking);

            //CLASS 100
            Node metaphysics = new Node("110", "Metaphysics");
            Node epistCausHumankind = new Node("120", "Epistemology, causation, and humankind");
            Node paraOccult = new Node("130", "Parapsychology and occultism");
            philPsych.addNode(bibliography);
            philPsych.addNode(libraryAndInfoSciences);
            philPsych.addNode(genEncyclopedicWorks);

            Node space = new Node("100.1", "Space");
            Node time = new Node("100.2", "Time");
            philPsych.addNode(space);
            philPsych.addNode(time);

            //CLASS 200
            Node philoTheo = new Node("210", "Philosophy and theory of religion");
            Node Bible = new Node("220", "The Bible");
            Node Christianity = new Node("230", "Christianity");
            religion.addNode(philoTheo);
            religion.addNode(Bible);
            religion.addNode(Christianity);

            Node beliefs = new Node("200.1", "Beliefs");
            Node ethics = new Node("200.2", "Ethics");
            religion.addNode(beliefs);
            religion.addNode(ethics);

            //CLASS 300
            Node statistics = new Node("310", "Statistics");
            Node polScie = new Node("320", "Political Science");
            Node economics = new Node("330", "Economics");
            socScie.addNode(statistics);
            socScie.addNode(polScie);
            socScie.addNode(economics);

            Node probability = new Node("300.1", "Probability");
            Node correlation = new Node("300.2", "Correlation");
            socScie.addNode(probability);
            socScie.addNode(correlation);

            //CLASS 400
            Node linguistics = new Node("410", "Linguistics");
            Node engOldEng = new Node("420", "English and Old English Languages");
            Node german = new Node("430", "German and other related languages");
            lang.addNode(statistics);
            lang.addNode(polScie);
            lang.addNode(economics);

            Node morphology = new Node("400.1", "Morphology");
            Node syntax = new Node("400.2", "Syntax");
            lang.addNode(morphology);
            lang.addNode(syntax);

            //CLASS 500
            Node mathematics = new Node("510", "Mathematics");
            Node astronomy = new Node("520", "Astronomy");
            Node physics = new Node("530", "Physics");
            science.addNode(mathematics);
            science.addNode(astronomy);
            science.addNode(physics);

            Node geometry = new Node("500.1", "Geometry");
            Node calculus = new Node("500.2", "Calculus");
            science.addNode(geometry);
            science.addNode(calculus);

            //CLASS 600
            Node mathematics = new Node("610", "Medicine and health");
            Node astronomy = new Node("620", "Engineering");
            Node physics = new Node("630", "Agriculture");
            tech.addNode(mathematics);
            tech.addNode(astronomy);
            tech.addNode(physics);

            Node internet = new Node("600.1", "Internet");
            Node trends = new Node("600.2", "Trends");
            tech.addNode(internet);
            tech.addNode(trends);
            
            //CLASS 700
            Node planning = new Node("710", "Area planning and landscape architecture");
            Node archi = new Node("720", "Architecture");
            Node sculpt = new Node("730", "Sculpture, ceramics and metalwork");
            science.addNode(planning);
            science.addNode(archi);
            science.addNode(sculpt);

            Node design = new Node("700.1", "Design");
            Node development = new Node("700.2", "Development");
            arts.addNode(design);
            arts.addNode(development);

            //CLASS 800
            Node americanLit = new Node("810", "American literature in English");
            Node engOldEngList = new Node("820", "English and Old English literatures");
            Node germanLit = new Node("830", "German and related literatures");
            lit.addNode(americanLit);
            lit.addNode(engOldEngList);
            lit.addNode(germanLit);

            Node litTheo = new Node("800.1", "Literary theory");
            Node litCrit = new Node("800.2", "Literary criticism");
            lit.addNode(litTheo);
            lit.addNode(litCrit);

            //CLASS 900
            Node geoTravel = new Node("910", "Geography and travel");
            Node bioGenealogy = new Node("920", "Biography and Genealogy");
            Node ancientHistory = new Node("930", "History of ancient world (to c. 499)");
            histoGeo.addNode(geoTravel);
            histoGeo.addNode(bioGenealogy);
            histoGeo.addNode(ancientHistory);
            
            Node natureEnv = new Node("900.1", "Nature and environment");
            Node climate = new Node("900.2", "Climate");
            histoGeo.addNode(natureEnv);
            histoGeo.addNode(climate);

            //adds the nodes
            Parent.addNode(genWorks);
            Parent.addNode(philPsych);
            Parent.addNode(religion);
            Parent.addNode(socScie);
            Parent.addNode(lang);
            Parent.addNode(science);
            Parent.addNode(tech);
            Parent.addNode(arts);
            Parent.addNode(lit);
            Parent.addNode(histoGeo);
        }

        private void SetBrowseBooks(Node BrowseBooks){
            
        }
        private void SetExit(Node Exit){
            Exit.setAction(unused -> { 
                System.out.println("Goodbye!!!");
                System.exit(0);
            });
        }
        private void MoveForward(Node next_node){
            navigationStack.push(_curr);
            _curr = next_node;
            _curr.action.accept(null);
        }
        
        private void MoveBackward(){
            if(navigationStack.isEmpty()) {
                System.out.println("Stack empty");
                return;
            }
            _curr = navigationStack.pop();
            _curr.action.accept(null);
//            System.out.println(_curr.getName());
        }

        private void bookOptions(Node book) {
            while (true) {
                System.out.println("\nBook: " + book.getName());
                System.out.println("1 - Read");
                System.out.println("2 - Update");
                System.out.println("3 - Delete");
                System.out.println("4 - Back to Category");
                System.out.print("Enter your choice: ");

                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> System.out.println("Reading book: " + book.getName());
                    case "2" -> {
                        System.out.print("Enter new book name: ");
                        String newName = scanner.nextLine();
                        book.renameNode(newName);
                        System.out.println("Book updated successfully.");
                    }
                    case "3" -> {
                        _curr.removeNode(book);
                        System.out.println("Book deleted successfully.");
                        return;
                    }
                    case "4" -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

}
