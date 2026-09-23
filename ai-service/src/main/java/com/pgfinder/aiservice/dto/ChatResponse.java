package com.pgfinder.aiservice.dto;

import java.util.List;

public class ChatResponse {
    private String reply;
    private List<RecommendedHostel> recommendedHostels;

    public ChatResponse() {
    }

    public ChatResponse(String reply, List<RecommendedHostel> recommendedHostels) {
        this.reply = reply;
        this.recommendedHostels = recommendedHostels;
    }

    public static ChatResponseBuilder builder() {
        return new ChatResponseBuilder();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<RecommendedHostel> getRecommendedHostels() {
        return recommendedHostels;
    }

    public void setRecommendedHostels(List<RecommendedHostel> recommendedHostels) {
        this.recommendedHostels = recommendedHostels;
    }

    public static class ChatResponseBuilder {
        private String reply;
        private List<RecommendedHostel> recommendedHostels;

        public ChatResponseBuilder reply(String reply) {
            this.reply = reply;
            return this;
        }

        public ChatResponseBuilder recommendedHostels(List<RecommendedHostel> recommendedHostels) {
            this.recommendedHostels = recommendedHostels;
            return this;
        }

        public ChatResponse build() {
            return new ChatResponse(reply, recommendedHostels);
        }
    }

    public static class RecommendedHostel {
        private Long id;
        private String name;
        private String area;
        private String gender;

        public RecommendedHostel() {
        }

        public RecommendedHostel(Long id, String name, String area, String gender) {
            this.id = id;
            this.name = name;
            this.area = area;
            this.gender = gender;
        }

        public static RecommendedHostelBuilder builder() {
            return new RecommendedHostelBuilder();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public static class RecommendedHostelBuilder {
            private Long id;
            private String name;
            private String area;
            private String gender;

            public RecommendedHostelBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public RecommendedHostelBuilder name(String name) {
                this.name = name;
                return this;
            }

            public RecommendedHostelBuilder area(String area) {
                this.area = area;
                return this;
            }

            public RecommendedHostelBuilder gender(String gender) {
                this.gender = gender;
                return this;
            }

            public RecommendedHostel build() {
                return new RecommendedHostel(id, name, area, gender);
            }
        }
    }
}
