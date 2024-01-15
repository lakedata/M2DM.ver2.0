package ddwu.project.mdm_ver2.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ddwu.project.mdm_ver2.domain.User;
import ddwu.project.mdm_ver2.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class KakaoService {

    private UserRepository userRepository;

    public String getAccessToken(String code) {

        String access_token = "";
        String refresh_token = "";
        String request_url = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(request_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요한 parameter 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            sb.append("grant_type=authorization_code");
            sb.append("&client_id=bbb0d5e603062dd02da05a9fe89b0c1e"); // REST_API_KEY
            sb.append("&redirect_uri=http://localhost:8080/kakao"); // redirect uri
            sb.append("&code=" +code);

            bw.write(sb.toString());
            bw.flush();

            // success: code = 200
            int responseCode = conn.getResponseCode();
            System.out.println("response code: " +responseCode);

            // 요청으로 얻은 JSON type Response message
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while((line = br.readLine()) != null) {
                result += line;
            }

            System.out.println("response body: " +result);

            // JSON Parsing 객체 생성 (Gson 라이브러리에 포함된 클래스 사용)
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access token: " +access_token);
            System.out.println("refresh token: " +refresh_token);

            br.close();
            bw.close();

        } catch(IOException e) {
            e.printStackTrace();
        }

        return access_token;
    }

    public HashMap<String, Object> getKakaoUserInfo(String token) {

        HashMap<String, Object> userInfo = new HashMap<>();

        String request_url = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(request_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); // request header setting

            // success: code = 200
            int responseCode = conn.getResponseCode();
            System.out.println("response code: " + responseCode);

            // 요청으로 얻은 JSON type Response message
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            System.out.println("response body: " + result);

            // JSON Parsing 객체 생성 (Gson 라이브러리에 포함된 클래스 사용)
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            long userCode = element.getAsJsonObject().get("id").getAsLong();
            String kakaoEmail = kakaoAccount.getAsJsonObject().get("email").getAsString();
            String kakaoProfileImg = properties.getAsJsonObject().get("profile_image").getAsString();

            userInfo.put("userCode", userCode);
            userInfo.put("kakaoEmail", kakaoEmail);
            userInfo.put("kakaoProfileImg", kakaoProfileImg);

            System.out.println("userCode: " + userCode);
            System.out.println("kakaoEmail: " + kakaoEmail);
            System.out.println("kakaoProfileImg: " + kakaoProfileImg);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean userExist = userRepository.existsByUserCode((long) userInfo.get("userCode"));

        if(!userExist) {
            System.out.println("user is not exist\nsaving ...");
            userInfo.put("newUser", true);
            User user = new User((long) userInfo.get("userCode"), userInfo.get("kakaoEmail").toString(), userInfo.get("kakaoProfileImg").toString());
            userRepository.saveAndFlush(user);

        } else {
            System.out.println("user is already exist !");
            userInfo.put("newUser", false);
        }

        return userInfo;
    }

    public void setUserNickname(long userCode, String nickname) {
        User user = userRepository.findByUserCode(userCode);
        user.setUserNickname(nickname);
        userRepository.saveAndFlush(user);
    }

    public void deleteUser(long userCode) {
        userRepository.deleteByUserCode(userCode);
    }
}
