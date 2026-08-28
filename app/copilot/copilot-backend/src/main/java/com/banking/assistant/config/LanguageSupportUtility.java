package com.banking.assistant.business.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/*

* Optional language utility.
* Use only when the application needs language detection
* or localized display labels.
*
* This class is independent from the existing business logic.
  */
  public final class LanguageSupportUtility {

  private static final String DEFAULT_LANGUAGE = "en";

  private static final Map<String, String> SUPPORTED_LANGUAGES;

  static {
  Map<String, String> languages = new LinkedHashMap<>();
  languages.put("en", "English");
  languages.put("kn", "Kannada");
  languages.put("hi", "Hindi");
  languages.put("ta", "Tamil");
  languages.put("te", "Telugu");

  
   SUPPORTED_LANGUAGES = Collections.unmodifiableMap(languages);
  

  }

  private LanguageSupportUtility() {
  // Utility class; prevent instantiation.
  }

  public static boolean isSupported(String languageCode) {
  if (languageCode == null || languageCode.isBlank()) {
  return false;
  }

  
   return SUPPORTED_LANGUAGES.containsKey(
           normalize(languageCode)
   );
  

  }

  public static String normalize(String languageCode) {
  if (languageCode == null || languageCode.isBlank()) {
  return DEFAULT_LANGUAGE;
  }

  
   return languageCode.trim()
           .toLowerCase(Locale.ROOT);
  

  }

  public static String getLanguageName(String languageCode) {
  String normalizedCode = normalize(languageCode);

  
   return SUPPORTED_LANGUAGES.getOrDefault(
           normalizedCode,
           SUPPORTED_LANGUAGES.get(DEFAULT_LANGUAGE)
   );
  

  }

  public static String resolveLanguage(String requestedLanguage) {
  String normalizedCode = normalize(requestedLanguage);

  
   return isSupported(normalizedCode)
           ? normalizedCode
           : DEFAULT_LANGUAGE;
  

  }

  public static Map<String, String> getSupportedLanguages() {
  return SUPPORTED_LANGUAGES;
  }

  /*

  * Optional helper.
  * Use only when a Locale object is required by another component.
    */
    public static Locale toLocale(String languageCode) {
    String language = resolveLanguage(languageCode);
    return Locale.forLanguageTag(language);
    }
    }
