import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IPCMapper {
    private Map<String, String> ipcMapping;

    public IPCMapper() {
        ipcMapping = new HashMap<>();
        readAndParseFile("D:\\学习\\学习文件\\AAA本基课题\\IPC分类表\\2024.01版IPC分类表-A部.txt");
    }

    private void readAndParseFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip lines that are not relevant
                if (line.trim().isEmpty() || line.contains("目录")) {
                    continue;
                }
                // Extract IPC code and description
                if (line.matches("^[A-Z][0-9]{2}[A-Z]?.*")) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length == 2) {
                        String prefix = parts[0].trim();
                        String mapping = parts[1].trim().replaceAll("；", ",");
                        if (prefix != null && !prefix.isEmpty()) {
                            String key = prefix.length() > 3 ? prefix.substring(0, 4) : prefix.substring(0, 3);
                            ipcMapping.put(key, mapping);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMappingToFile(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : ipcMapping.entrySet()) {
                bw.write(entry.getKey() + "：" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IPCMapper mapper = new IPCMapper();
        // Save the mapping to a file
        mapper.saveMappingToFile("D:\\学习\\学习文件\\AAA本基课题\\IPC分类表\\OUTPUT\\ipc_mapping_output_A.txt");
    }
}
