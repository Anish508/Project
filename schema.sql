-- ========================================================
-- ALUMNI NETWORKING PLATFORM - SUPABASE POSTGRESQL SCHEMA
-- Execute this script in Supabase SQL Editor (SQL Query Runner)
-- ========================================================

-- 1. Base Users Table (stores profile info linked to auth.users)
CREATE TABLE IF NOT EXISTS public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('student', 'alumni', 'admin')),
    full_name TEXT NOT NULL,
    avatar_url TEXT,
    phone TEXT,
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Student Profiles Table
CREATE TABLE IF NOT EXISTS public.student_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES public.users(id) ON DELETE CASCADE,
    batch_year INTEGER NOT NULL,
    department TEXT NOT NULL,
    roll_number TEXT,
    current_semester INTEGER,
    bio TEXT,
    skills TEXT,
    resume_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. Alumni Profiles Table
CREATE TABLE IF NOT EXISTS public.alumni_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES public.users(id) ON DELETE CASCADE,
    graduation_year INTEGER NOT NULL,
    department TEXT NOT NULL,
    current_company TEXT,
    designation TEXT,
    industry TEXT,
    location TEXT,
    bio TEXT,
    linkedin_url TEXT,
    available_for_mentorship BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. Admin Profiles Table
CREATE TABLE IF NOT EXISTS public.admin_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES public.users(id) ON DELETE CASCADE,
    department TEXT,
    admin_level TEXT DEFAULT 'Standard',
    permissions TEXT DEFAULT 'Full',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. Jobs Table
CREATE TABLE IF NOT EXISTS public.jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    posted_by UUID REFERENCES public.users(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    company TEXT NOT NULL,
    location TEXT NOT NULL,
    job_type TEXT NOT NULL DEFAULT 'Full-time', -- Full-time, Internship, Part-time, Remote
    salary_range TEXT,
    description TEXT NOT NULL,
    requirements TEXT,
    application_email TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 6. Events Table
CREATE TABLE IF NOT EXISTS public.events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by UUID REFERENCES public.users(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    event_date TEXT NOT NULL,
    event_time TEXT NOT NULL,
    location_type TEXT NOT NULL DEFAULT 'Online', -- Online, On-Campus, Hybrid
    location_details TEXT NOT NULL,
    category TEXT DEFAULT 'General',
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 7. Mentorship Requests Table
CREATE TABLE IF NOT EXISTS public.mentorship_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mentor_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    mentee_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    topic TEXT NOT NULL,
    message TEXT NOT NULL,
    status TEXT DEFAULT 'pending', -- pending, accepted, rejected
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 8. Posts / Discussion Feed Table
CREATE TABLE IF NOT EXISTS public.posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    post_type TEXT DEFAULT 'discussion', -- announcement, discussion, story
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Row Level Security (RLS) Enablement
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.alumni_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.jobs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.mentorship_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.posts ENABLE ROW LEVEL SECURITY;

-- Allow Public / Authenticated read access
CREATE POLICY "Allow public read users" ON public.users FOR SELECT USING (true);
CREATE POLICY "Allow user self insert" ON public.users FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow user self update" ON public.users FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Allow public read student_profiles" ON public.student_profiles FOR SELECT USING (true);
CREATE POLICY "Allow student profile insert" ON public.student_profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow student profile update" ON public.student_profiles FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Allow public read alumni_profiles" ON public.alumni_profiles FOR SELECT USING (true);
CREATE POLICY "Allow alumni profile insert" ON public.alumni_profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow alumni profile update" ON public.alumni_profiles FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Allow public read admin_profiles" ON public.admin_profiles FOR SELECT USING (true);
CREATE POLICY "Allow admin profile insert" ON public.admin_profiles FOR INSERT WITH CHECK (true);

CREATE POLICY "Allow public read jobs" ON public.jobs FOR SELECT USING (true);
CREATE POLICY "Allow auth insert jobs" ON public.jobs FOR INSERT WITH CHECK (auth.role() = 'authenticated');
CREATE POLICY "Allow auth update jobs" ON public.jobs FOR UPDATE USING (auth.uid() = posted_by);

CREATE POLICY "Allow public read events" ON public.events FOR SELECT USING (true);
CREATE POLICY "Allow auth insert events" ON public.events FOR INSERT WITH CHECK (auth.role() = 'authenticated');

CREATE POLICY "Allow read mentorship_requests" ON public.mentorship_requests FOR SELECT USING (auth.uid() = mentor_id OR auth.uid() = mentee_id);
CREATE POLICY "Allow insert mentorship_requests" ON public.mentorship_requests FOR INSERT WITH CHECK (auth.role() = 'authenticated');
CREATE POLICY "Allow update mentorship_requests" ON public.mentorship_requests FOR UPDATE USING (auth.uid() = mentor_id);

CREATE POLICY "Allow public read posts" ON public.posts FOR SELECT USING (true);
CREATE POLICY "Allow auth insert posts" ON public.posts FOR INSERT WITH CHECK (auth.role() = 'authenticated');
