package com.amw.datawave.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, User user) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setName(user.getName());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setRole(user.getRole());
        return userRepository.save(userToUpdate);
    }


    public byte[] exportUsersToJson() throws IOException {
        List<User> users = userRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(users);

        return jsonData.getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importUsersFromJson(String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = objectMapper.readValue(jsonData, new TypeReference<List<User>>() {
        });

        userRepository.saveAll(users);
    }


    public byte[] exportUsersToXml() throws Exception {
        List<User> users = userRepository.findAll();

        JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        StringWriter sw = new StringWriter();
        for (User user : users) {
            jaxbMarshaller.marshal(user, sw);
        }
        String xmlData = sw.toString();

        return xmlData.getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importUsersFromXml(String xmlData) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xmlData);
        User user = (User) jaxbUnmarshaller.unmarshal(reader);

        userRepository.save(user);
    }
}
