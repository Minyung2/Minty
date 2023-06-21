import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Pagination from './pagination';
import { Link, useParams, useNavigate } from 'react-router-dom';

function JobList(){
    const [jobList, setJobList] = useState([]);
    const { page: pageParam, searchText } = useParams();
    const [currentPage, setCurrentPage] = useState(pageParam ? Number(pageParam) : 1);
    const [totalPages, setTotalPages] = useState(0);
    const navigate = useNavigate();
    const setCurrentPageAndNavigate = (pageNumber) => {
        setCurrentPage(pageNumber);
        navigate(`/boardList/${boardType ? `${boardType}/` : ''}${categoryId ? `category/${categoryId}/` : ''}${pageNumber}`);
    };

    const fecthData = async () => {
        let endpoint;
        if(searchText){
            endpoint = `/api/jobList/search/${searchText}/${currentPage}`;
        }else{
            endpoint = `/api/jobList/${currentPage}`;
        }
        await axios
        .get(endpoint)
        .then((response) => {
            let jobs = [...response.data.jobList];
            let total = response.data.totalPages;
            setJobList(jobs);
            setTotalPages(total);
        })
         .catch((error) => {
                console.error('Error fetching data:', error);
              });
    };

    return()
}

export default JobList;