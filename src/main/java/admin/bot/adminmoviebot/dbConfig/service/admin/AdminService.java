package admin.bot.adminmoviebot.dbConfig.service.admin;

import admin.bot.adminmoviebot.bot.constants.LanguageCode;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.AdminDetails;
import admin.bot.adminmoviebot.dbConfig.repository.AdminDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

 private final AdminDetailsRepository adminDetailsRepository;

 public AdminService(AdminDetailsRepository adminDetailsRepository) {
  this.adminDetailsRepository = adminDetailsRepository;
 }

 public void editFullName(String text) {
 AdminDetails adminDetails =  getAdmin();
 adminDetails.setFullName(text);
 adminDetailsRepository.save(adminDetails);
 }

 private AdminDetails getAdmin() {
  Optional<AdminDetails> optional = adminDetailsRepository.findById(LanguageCode.CNS_ADMIN_ID);
  return optional.get();
 }

 public void editUsername(String text) {
  AdminDetails adminDetails = getAdmin();
  adminDetails.setTgUserName(text);
  adminDetailsRepository.save(adminDetails);
 }

 public void editPhoneNumber(String text) {
  AdminDetails adminDetails = getAdmin();
  adminDetails.setPhoneNumber(text);
  adminDetailsRepository.save(adminDetails);
 }
}
