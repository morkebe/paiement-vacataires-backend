package sn.ufr.vacations.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<>();
    }
    
    public Map<String, Object> getAssistantDashboard() {
        return new HashMap<>();
    }
    
    public Map<String, Object> getVacataireDashboard() {
        return new HashMap<>();
    }
    
    public Map<String, Object> getFinancierDashboard() {
        return new HashMap<>();
    }
}
