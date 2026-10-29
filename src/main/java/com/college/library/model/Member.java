package com.college.library.model;

import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

/**
 * Represents a library member who can borrow books.
 * <p>
 * Each member has a unique ID, a name, and an email address.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class Member {

    /** The unique member identifier. */
    private final String memberId;

    /** The member's full name. */
    private String name;

    /** The member's email address. */
    private String email;

    /**
     * Constructs a new Member with the given details.
     *
     * @param memberId the unique member ID (must not be blank)
     * @param name     the member's name (must not be blank)
     * @param email    the member's email (must not be blank)
     * @throws IllegalArgumentException if any parameter is blank
     */
    public Member(String memberId, String name, String email) {
        if (StringUtils.isBlank(memberId)) {
            throw new IllegalArgumentException("Member ID must not be blank");
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        this.memberId = memberId.trim();
        this.name = name.trim();
        this.email = email.trim();
    }

    /** @return the unique member ID */
    public String getMemberId() {
        return memberId;
    }

    /** @return the member's name */
    public String getName() {
        return name;
    }

    /**
     * Sets the member's name.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the member's email */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the member's email.
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Two members are equal if they share the same member ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("Member{id='%s', name='%s', email='%s'}",
                memberId, name, email);
    }
}

