package controller.utils;

public enum TextTypeEnum {
    PersonalExperience {
        @Override
        public String toString() {
            return "PersonalExperience";
        }
    }, Promotion {
        @Override
        public String toString() {
            return "Promotion";
        }
    }
}
