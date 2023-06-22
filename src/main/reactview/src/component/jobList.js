import React, { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Pagination from './Pagination';

function JobList() {
  const [jobs, setJobs] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchText, setSearchText] = useState(null);
  const { page: pageParam } = useParams();
  const [currentPage, setCurrentPage] = useState(pageParam ? Number(pageParam) : 1);

  const navigate = useNavigate();



     useEffect(() => {
        fetchJobList();
      }, []);

 const fetchJobList = () => {
   const url = searchText ? `/api/jobList/searchQuery/${searchText}/${currentPage}` : `/api/jobList/${currentPage}`;

   axios
     .get(url)
     .then((response) => {
       const data = response.data;
       setJobs(data.jobPage.content);
       setTotalPages(data.totalPages);
     })
     .catch((error) => {
       console.error('Error fetching job list:', error);
     });
 };

  const handleSearch = (e) => {
        e.preventDefault();
        setCurrentPage(1);
        setSearchText(e.target.value);
        fetchJobList();
      };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    fetchJobList();
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input type="text" value={searchText} onChange={(e) => setSearchText(e.target.value)} />
        <button type="submit">Search</button>
      </form>

      <ul>
        {jobs.map((job) => (
          <li key={job.id}>{job.title}</li>
        ))}
      </ul>

      <Pagination totalPages={totalPages} currentPage={currentPage} setCurrentPage={handlePageChange} />
    </div>
  );
}

export default JobList;
