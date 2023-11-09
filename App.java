package com.pwdmgr.passwordmanagerv1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.*;
import java.io.*;

public class App extends Application {
    private Stage primaryStage;
    private ListView<String> websiteListView;
    private TextArea detailsArea;
    private Connection connection;

    Scene mainScene;
    private boolean flag = false;
    ObservableList<String> websites;
    ObservableList<String> websiteList;
    ObservableList<Integer> Identifier;
    SQLLinker MJ = new SQLLinker();
    String filePath = "C:/Sambhav/Manipal/Year 2/3rd Semester/OOPs LAB/Mini Project/FinalProject/PasswordManagerV1/AESKey";
    SecretKey secretKey;

    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Password Manager");

        if (connectToDatabase()) {
            if (authenticateUser()) {
            setupMainApplication();
            }
        }
    }

    private boolean connectToDatabase() {

        connection = MJ.connectsql();
        return true;
    }

    private boolean authenticateUser() {
        Stage authStage = new Stage();

        //An application-modal stage blocks input events to all other stages in the application until it's closed.
        authStage.initModality(Modality.APPLICATION_MODAL);

        //Associating the authentication stage with the main stage in this way can help in managing the application's windows and ensuring proper behavior in the context of modal dialogs.
        authStage.initOwner(primaryStage);
        authStage.setTitle("Authentication");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();


        Button loginButton = new Button("Login");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if ("DEMO".equals(username) && "PASSWORD".equals(password)) {
                authStage.close();
                flag = true;
            } else {
                showErrorAlert("Authentication Failed", "Invalid username or password.");
            }
        });



        VBox authLayout = new VBox(10);
        authLayout.setPadding(new Insets(10));
        authLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        Scene authScene = new Scene(authLayout);
        authStage.setScene(authScene);
        authStage.showAndWait();

        return flag;
    }

    private void setupMainApplication() {
        primaryStage.setTitle("Password Manager");

        websiteListView = new ListView<>();

        websiteListView.getItems().addAll(retrieveWebsites());

        websiteListView.setOnMouseClicked(e -> showDetails());

        Button addPasswordButton = new Button("Add Website");
        Button modifyDetails = new Button("Modify Details");
        Button deleteWebsite = new Button("Delete Websites");

        VBox rootPForm = new VBox(10);

        detailsArea = new TextArea("Welcome to Password Manager!");
        websiteList = retrieveWebsites();
//        websiteListView = new ListView<>(websiteList);

        detailsArea.setEditable(false);
        detailsArea.setMaxWidth(350);
        detailsArea.setMaxHeight(250);

        rootPForm.getChildren().addAll(detailsArea, addPasswordButton, modifyDetails, deleteWebsite);

        Label webs = new Label("Website:");
        TextField websField = new TextField();

        Label user = new Label("Username:");
        TextField userField = new TextField();

        Label pwd = new Label("Password:");
        PasswordField pwdField = new PasswordField();

        addPasswordButton.setOnAction(e ->
        {
            Button SaveButton = new Button("Save");
            Button backButton = new Button("Back");

            backButton.setOnAction(ae5 ->
            {
                rootPForm.getChildren().removeAll(webs, websField, user, userField, pwd, pwdField, SaveButton, backButton);
                rootPForm.getChildren().addAll(addPasswordButton,modifyDetails, deleteWebsite);
            });

            SaveButton.setOnAction(ae ->
            {
                AddDetails(websField, userField, pwdField);
                rootPForm.getChildren().removeAll(webs, websField, user, userField, pwd, pwdField, SaveButton, backButton);
                rootPForm.getChildren().addAll(addPasswordButton,modifyDetails, deleteWebsite);

            });
            rootPForm.getChildren().removeAll(addPasswordButton, modifyDetails, deleteWebsite);
            rootPForm.getChildren().addAll(webs, websField, user, userField, pwd, pwdField, SaveButton, backButton);
            websiteListView.refresh();

        });

        boolean[] flag = new boolean[1];
        //Modify existing details of the database
        modifyDetails.setOnAction(e ->
        {
            Identifier = FXCollections.observableArrayList();
            ResultSet rs1 = fetchmysqlquery("SELECT Serial_Number FROM Password;");
            try {
                while (rs1.next()) {
                    flag[0] = true;
                    int S = rs1.getInt("Serial_Number");
                    Identifier.add(S);
                }
            }

            catch (Exception ignore){flag[0] = false;}

            if(flag[0]) {
                ComboBox<Integer> comboBox = new ComboBox<>(Identifier);
                comboBox.getSelectionModel().selectFirst();
                Label ComboText = new Label("Select Website Identifier: ");

                CheckBox webCheckBox = new CheckBox("Website Name");
                CheckBox usrCheckBox = new CheckBox("User Name");
                CheckBox pwdCheckBox = new CheckBox("Password");

                Button backButton = new Button("Back");

                Button updWeb = new Button("Update Website");
                Button updUsr = new Button("Update Username");
                Button updPass = new Button("Update Password");


                backButton.setOnAction(ae6 ->
                {
                    rootPForm.getChildren().addAll(addPasswordButton, modifyDetails, deleteWebsite);
                    rootPForm.getChildren().removeAll(webCheckBox, usrCheckBox, pwdCheckBox, webs, websField, updWeb, user, userField, updUsr, updPass, pwd, pwdField, backButton, ComboText, comboBox);

                });

                webCheckBox.setOnAction(e1 ->
                {
                    rootPForm.getChildren().addAll(webs, websField, updWeb);
                    updWeb.setOnAction(ae45 ->
                    {
                        String oldwebsite, serial = null;
                        ResultSet resultSetfetch = fetchmysqlquery(String.format("SELECT Serial_Number, Website FROM Password WHERE Serial_Number = %d",comboBox.getSelectionModel().getSelectedItem()));
                        try {
                            resultSetfetch.next();
                            serial = resultSetfetch.getString("Serial_Number");
                            oldwebsite = resultSetfetch.getString("Website") + "("+serial+")";

                        }

                        catch(Exception ignore){oldwebsite = null;}

                        String n = String.format("UPDATE password SET Website = \"%s\" WHERE Serial_Number=%d", websField.getText(), comboBox.getSelectionModel().getSelectedItem());
                        System.out.println(n);
                        executemysqlquery(n);
                        if(!websiteList.isEmpty()) {
                            System.out.println(("WebsiteList: " + websiteList));
                            System.out.println("Old Website: " + oldwebsite);
                            int idx = websiteList.indexOf(oldwebsite);
                            System.out.println("Index of Old Website: " + idx);

                            System.out.println("INDEX: " + idx);
                            System.out.println("Selected Text: " + websiteList.get(idx));

                            System.out.println("Removed Element: " + websiteList.remove(idx));

                            System.out.println(("WebsiteList AFTER CHANGING: " + websiteList));
                            websiteList.add(idx, websField.getText() +"("+serial+")");

                            System.out.println(websiteList);
                            websiteListView.setItems(websiteList);
                            showConfirmationAlert("Success", "Website Name Updated");
                        }
                        else {
                            websiteList.add(0, websField.getText());
                        }
                            rootPForm.getChildren().removeAll(webCheckBox, webs, websField, updWeb);
//                        }
                    });
                    rootPForm.getChildren().removeAll(webCheckBox);
                });

                usrCheckBox.setOnAction(e1 ->
                {
                    rootPForm.getChildren().addAll(user, userField, updUsr);
                    updUsr.setOnAction(aeu ->
                    {
                        String n = String.format("UPDATE password SET UserName = \"%s\" WHERE Serial_Number=%d", userField.getText(), comboBox.getSelectionModel().getSelectedItem());
                        System.out.println(n);
                        if (executemysqlquery(n)) {
                            showConfirmationAlert("Success", "Username Updated");
                        }
                        rootPForm.getChildren().removeAll(usrCheckBox, user, userField, updUsr);
                    });
                    rootPForm.getChildren().removeAll(usrCheckBox);
                });

                pwdCheckBox.setOnAction(e1 ->
                {
                    rootPForm.getChildren().addAll(pwd, pwdField, updPass);
                    updPass.setOnAction(aep ->
                    {
//
                        String n = String.format("SELECT Website, Alias FROM Password WHERE Serial_Number = %d", comboBox.getSelectionModel().getSelectedItem());
                        ResultSet rs2 = fetchmysqlquery(n);
                        try {
                            rs2.next();
                            String Alias = rs2.getString("Alias");
                            String web = rs2.getString("Website");
                            EncryptedPasswordStoreinSQL(Alias, pwdField.getText(), comboBox.getSelectionModel().getSelectedItem());

                        } catch (Exception ex) {}
                        rootPForm.getChildren().removeAll(pwd, pwdField, updPass);
                        showConfirmationAlert("Success", "Password Updated");
                    });
                    rootPForm.getChildren().removeAll(pwdCheckBox);
                });
                rootPForm.getChildren().removeAll(addPasswordButton, modifyDetails, deleteWebsite);
                rootPForm.getChildren().addAll(ComboText, comboBox, webCheckBox, usrCheckBox, pwdCheckBox, backButton);
            }
            else {
                showErrorAlert("Database Error", "Enter data first");
            }
        });

        Button del = new Button("Delete");

        deleteWebsite.setOnAction(ae3->
        {
            Identifier = FXCollections.observableArrayList();
            ResultSet rs1 = fetchmysqlquery("SELECT Serial_Number FROM Password;");
            try {
                while (rs1.next()) {
                    int S = rs1.getInt("Serial_Number");
                    System.out.println("Identifier me jaa rhaa hai ya nahi: " + Identifier.add(S));
                    flag[0] = true;
                }
            }

            catch (Exception ignore){
                System.out.println("In Exception, flag is now false and hence websites wont be dleeted");
                flag[0] = false;
            }
            System.out.println("FLAG 0 VALUE: " + flag[0]);
            if(flag[0])
            {
                Identifier = FXCollections.observableArrayList();
                ComboBox<Integer> comboBox = new ComboBox<>(Identifier);
                comboBox.getSelectionModel().selectFirst();
                Label ComboText = new Label("Select Website Identifier to delete: ");

                Button backButton = new Button("Back");
                backButton.setOnAction(ae5 ->
                {
                    rootPForm.getChildren().removeAll(comboBox,ComboText,del,backButton);
                    rootPForm.getChildren().addAll(addPasswordButton,modifyDetails, deleteWebsite);
                });

                ResultSet rs12 = fetchmysqlquery("SELECT Serial_Number FROM Password;");
                try {
                    while (rs12.next()) {
                        flag[0] = true;
                        int S = rs12.getInt("Serial_Number");
                        Identifier.add(S);
                    }
                }
                catch (Exception ignore){flag[0] = false;}
                System.out.println("In DeletePassword, ComboBox Setup");
                del.setOnAction(aed->
                {
                    int i;
                    System.out.println("Inside Delete Button");
                    String webName, userName, combined = null, alias1 = null;
                    int Snumber;
                    if (flag[0]) {
                        System.out.println("Inside FLAG if Statement");
                        Snumber = comboBox.getSelectionModel().getSelectedItem();
                        String n = String.format("SELECT Website, UserName, Alias FROM Password WHERE Serial_Number = %d", Snumber);
                        ResultSet rs3 = fetchmysqlquery(n);

                        try {
                            rs3.next();
                            webName = rs3.getString("Website");
                            userName = rs3.getString("UserName");
                            alias1 = rs3.getString("Alias");
                            combined = webName + "\n" + userName;
                        } catch (Exception ignored) {
                        }
                        
                        //Creating Alias's Array
                        ResultSet AliasArray = fetchmysqlquery("SELECT Alias FROM Password");
                        List<String> AliasArr = new ArrayList<>();
                        try {
                            while (AliasArray.next()) {
                                AliasArr.add(AliasArray.getString("Alias"));
                            }
                        }
                        catch (Exception ign){}

                        System.out.println("Alias Array: " + AliasArr);

                        for(i =0 ; i<AliasArr.toArray().length ; i++)
                        {
                            if(AliasArr.get(i).equals(alias1))
                            {
                                System.out.printf("aliases match at index %d\nAliasArray ka Alias: %s\nAlias of the retrieved record: %s%n",i, AliasArr.get(i), alias1);
                                break;
                            }
                        }

                        System.out.println(" DETAILS TO BE DELETED " + combined);
                        System.out.println("SNUMBER: " + Snumber);
//                        System.out.println(AlertYesNo("Confirmation", "Do you want to delete the following content?"));
                        if(AlertYesNo("Confirmation", "Do you want to delete the following content?" + combined))
                        {
                            String seqel = String.format("DELETE FROM Password WHERE Serial_Number = %d", Snumber);
                            System.out.println(seqel);
                            executemysqlquery(seqel);

                            if(!websiteList.isEmpty())
                            {
                                System.out.println(websiteList);
                                websiteList.remove(i);
                            }
                            System.out.println(websiteList);
                            websiteListView.setItems(websiteList);
                            detailsArea.setText("Welcome to Password Manager!");

                        }
                    }
                    rootPForm.getChildren().removeAll(ComboText, comboBox, del, backButton);
                    rootPForm.getChildren().addAll(addPasswordButton, modifyDetails, deleteWebsite);
                });
                rootPForm.getChildren().removeAll(addPasswordButton, modifyDetails, deleteWebsite);
                rootPForm.getChildren().addAll(ComboText,comboBox, del, backButton);
            }
            else
            {
                showErrorAlert("Database Error", "Enter data first");
            }
            });


        HBox mainPane = new HBox(10);
        mainPane.getChildren().addAll(websiteListView, rootPForm);
        mainScene = new Scene(mainPane, 600, 400);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private ObservableList<String> retrieveWebsites() {
        websites = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT Website, Serial_Number FROM Password";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                websites.add(resultSet.getString("Website") + "(" + resultSet.getString("Serial_Number") + ")" );
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error retrieving website data.");
        }

        return websites;
    }

    private void showDetails() {
        String selectedWebsite = websiteListView.getSelectionModel().getSelectedItem();
        if (selectedWebsite != null) {
            String details = retrievePasswordDetails(selectedWebsite);
            detailsArea.setText(details);
        }
    }

    private String retrievePasswordDetails(String website) {
        try {
            
            String substring = website.substring(0, OpenBracketFinder(website));
            String sQlQuery = String.format("SELECT Serial_Number, UserName, HashedPass, Alias FROM Password WHERE Website = \"%s\" AND Serial_Number = %s", substring, website.substring(OpenBracketFinder(website)+1,CloseBracketFinder(website)));
            System.out.println(sQlQuery);

            PreparedStatement preparedStatement = connection.prepareStatement(sQlQuery);
            ResultSet resultSet = preparedStatement.executeQuery(sQlQuery);

            if(resultSet.next()) {
                String snumber = resultSet.getString("Serial_Number");
                String username = resultSet.getString("UserName");
                System.out.println(username);
                String encryptedPassword = resultSet.getString("HashedPass");
                System.out.println(encryptedPassword);
                String Alias = resultSet.getString("Alias");
                System.out.println(Alias);

                //Getting the Secret Key
                PwdCreator(Alias);

                //Decrypting Password
                String decryptedPassword = SecretKeyManipulation.decryptPassword(encryptedPassword, secretKey);

                return "Identifier: "+ snumber + "\nWebsite: " + substring + "\nUsername: " + username + "\nPassword: " + decryptedPassword;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Error retrieving password details.");
        }

        return "Identifier: Null\n" +"Website: " + website + "\nUsername: USERNAME\nPassword: PASSWORD";
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void stop() {
        MJ.closesql();
    }

    public boolean executemysqlquery(String s) {
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            ps.executeUpdate();
//            showConfirmationAlert("Success", String.format("%d rows affected", rs));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Not Possible");
            return false;
        }

    }

    public ResultSet fetchmysqlquery(String s) {
        ResultSet rs;
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            rs = ps.executeQuery();
//            showConfirmationAlert("Success", String.format("%d rows affected", rs));
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Not Possible");
            rs = null;
            return rs;
        }

    }

    public void AddDetails (TextField websField, TextField userField, PasswordField pwdField){
        String w = websField.getText();
        String u = userField.getText();
        String p = pwdField.getText();
        String ALIAS;

        System.out.println(w + " " + u + " " + p);

        fetchmysqlquery("SELECT Website FROM Password;");

        if (w.isEmpty() || u.isEmpty() || p.isEmpty()) {
            showWarningAlert("Warning", "Website Name/Username/Password is Empty! Please enter details.");
        }
        else {
            String sqlquery = String.format("INSERT INTO password(Website, UserName, HashedPass, Alias) VALUES (\"%s\",\"%s\", \"%s\" ,\"%s\")", w, u, p, p);

            executemysqlquery(sqlquery);
            System.out.println("HI");

        }

        try {
            PreparedStatement ps = connection.prepareStatement("Select Serial_Number FROM Password", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery();

            rs.last();

            int snum = rs.getInt("Serial_Number");
            String sno = rs.getString("Serial_Number");
            ALIAS = sno + w.substring(0,w.length()/2) + u.substring(0,u.length()/2);
            EncryptedPasswordStoreinSQL(ALIAS, p, snum);
            websiteListView.getItems().addAll(w+"("+sno+")");

        }

        catch (Exception ignored) {
            System.out.println(ignored.getMessage());
            System.out.println("EXCEPTION OCCURED");
        }
    }

    private void EncryptedPasswordStoreinSQL(String ALIAS, String p, int snum) throws Exception {
        PwdCreator(ALIAS);
        String encryptedPassword = SecretKeyManipulation.encryptPassword(p, secretKey);
        System.out.println("Snum: " + snum + "\nAlias: " + ALIAS + "\nSecretKey: " + secretKey + "\nEncrypted Pass: " + encryptedPassword );
        String sqlQ = String.format("UPDATE password SET HashedPass=\"%s\", Alias=\"%s\" WHERE Serial_Number=%d;",encryptedPassword, ALIAS, snum);
        if(executemysqlquery(sqlQ)){
                showConfirmationAlert("Success", "Details added Successfully!");
        }
    }

    private void PwdCreator(String ALIAS) throws Exception {
        File keyFile = SecretKeyManipulation.getAESKeyFile(filePath, ALIAS);

        if (keyFile.exists()) {
            // The .key file for the alias exists; you can load the key from the file here.
            secretKey = SecretKeyManipulation.loadAESKeyFromFile(keyFile);
        } else {
            // The .key file for the alias doesn't exist; you can generate the key and save it to the file.
            secretKey = SecretKeyManipulation.generateAESKey();
            SecretKeyManipulation.saveAESKeyToFile(keyFile, secretKey);
        }
    }

    private boolean AlertYesNo(String Title, String Content)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Content, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirmation Alert");
        alert.setContentText(Content);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            System.out.println("OK Button");
            return true;
        }
        else
        {
            System.out.println("Cancel Button");
            return false;
        }
    }

    private int OpenBracketFinder(String website)
    {
        int start_index = 0;
        System.out.println(website);
        for(int i = 0 ; i<website.length() ; i++)
        {
            if('(' == website.charAt(i))
            {
                start_index = i;
                break;
            }
        }
        return start_index;
    }

    private int CloseBracketFinder(String website)
    {
        int end_index = 0;
        System.out.println(website);
        for(int i = 0 ; i<website.length() ; i++)
        {
            if(')' == website.charAt(i))
            {
                end_index = i;
                break;
            }
        }
        return end_index;
    }

}



