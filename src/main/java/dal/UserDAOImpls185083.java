package dal;
import dal.dto.IUserDTO;
import dal.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO Rename class so it matches your study-number
public class UserDAOImpls185083 implements dal.IUserDAO {

    PreparedStatement preparedStatement;

    //TODO Make a connection to the database
    private Connection createConnection() throws SQLException {
        return  DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185083?"
                + "user=s185083&password=9lSB7UvGEA1ILNs4kRUgy");
    }

    @Override
    public void createUser(IUserDTO user) throws DALException  {
        //TODO Implement this - Should insert a user into the db using data from UserDTO object.

        String createUserQ = "INSERT INTO UserInfo (userid,username,userini,userrole) VALUES (?,?,?,?)";

        try (Connection c = createConnection()){

            String roleString = String.join(";", user.getRoles());
            preparedStatement = c.prepareStatement(createUserQ);
            preparedStatement.setInt(1, user.getUserId());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getIni());
            preparedStatement.setString(4, roleString);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public IUserDTO getUser(int userId) throws DALException {

        //TODO Implement this - should retrieve a user from db and parse it to a UserDTO

        String getUserQ = "SELECT * FROM UserInfo WHERE userid = ?";

        try (Connection c = createConnection()) {
            preparedStatement = c.prepareStatement(getUserQ);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            IUserDTO user = null;
            if (resultSet.next()){
                user = makeUserFromResultset(resultSet);
            }
            return user;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public List<IUserDTO> getUserList() throws DALException {
        //TODO Implement this - Should retrieve ALL users from db and parse the resultset to a List of UserDTO's.

        String getUserListQ = "SELECT * FROM UserInfo";

        try (Connection c = createConnection()){

            preparedStatement = c.prepareStatement(getUserListQ);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<IUserDTO> userList = new ArrayList<>();
            while (resultSet.next()){
                IUserDTO user = makeUserFromResultset(resultSet);
                userList.add(user);
            }
            return userList;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void updateUser(IUserDTO user) throws DALException {
        //TODO Implement this - Should update a user in the db using data from UserDTO object.

        String updateQ = "UPDATE UserInfo SET username = ?, userini = ?, userrole = ? WHERE userid = ?";

        try (Connection c = createConnection()){

            String roleString = String.join(";", user.getRoles());

            preparedStatement = c.prepareStatement(updateQ);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getIni());
            preparedStatement.setString(3, roleString);
            preparedStatement.setInt(4, user.getUserId());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        //TODO Implement this - Should delete a user with the given userid from the db.

        String query = "DELETE FROM UserInfo WHERE userid = ?";

        try (Connection c = createConnection()){

            preparedStatement = c.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    private IUserDTO makeUserFromResultset(ResultSet resultSet) throws SQLException {
        IUserDTO user = new UserDTO();
        user.setUserId(resultSet.getInt("userid"));
        user.setUserName(resultSet.getString("username"));
        user.setIni(resultSet.getString("userini"));
        //Extract roles as String
        String roleString = resultSet.getString("userrole");
        //Split string by ;
        String[] roleArray = roleString.split(";");
        //Convert to List
        List<String> roleList = Arrays.asList(roleArray);
        user.setRoles(roleList);
        return user;
    }
}