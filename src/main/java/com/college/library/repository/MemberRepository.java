package com.college.library.repository;

import com.college.library.model.Member;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Member persistence operations.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public interface MemberRepository {

    /**
     * Saves a member to the repository.
     * @param member the member to save
     */
    void save(Member member);

    /**
     * Finds a member by their unique ID.
     * @param memberId the member ID to search for
     * @return an Optional containing the member, or empty if not found
     */
    Optional<Member> findById(String memberId);

    /**
     * Returns all members in the repository.
     * @return a list of all members
     */
    List<Member> findAll();
}

