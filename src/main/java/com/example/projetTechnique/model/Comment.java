package com.example.projetTechnique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JsonIgnore
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateComment;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Post getPost() {
        return post;
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public Date getDateComment() {
        return dateComment;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    public String getContenu() {
        return contenu;
    }

    public void setDateComment(Date dateComment) {
        this.dateComment = dateComment;
    }
}
