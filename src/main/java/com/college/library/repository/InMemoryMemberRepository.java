package com.college.library.repository;

import com.college.library.model.Member;
import java.util.*;

/**
 * In-memory implementation of {@link MemberRepository}.
 * Uses a HashMap for O(1) member ID lookups.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class InMemoryMemberRepository implements MemberRepository {

    /** Internal storage: memberId -> Member mapping. */
    private final Map<String, Member> members = new HashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.getMemberId(), member);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }
}

