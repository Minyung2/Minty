import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Pagination from './pagination';
import { Button ,Container, Row, Col, Nav, Form } from 'react-bootstrap';
import '../css/boardList.css';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import { BiSearch } from 'react-icons/bi';


function BoardList() {
  const [topCategories, setTopCategories] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [tradeBoards, setTradeBoards] = useState([]);
  const { id: categoryId, page: pageParam } = useParams();
 const [searchQuery, setSearchQuery] = useState('');
 const [searchQueryInput, setSearchQueryInput] = useState('');
  const [currentPage, setCurrentPage] = useState(pageParam ? Number(pageParam) : 1);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();
  const [selectedSubCategory, setSelectedSubCategory] = useState(null);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const [minPriceInput, setMinPriceInput] = useState(null);
  const [maxPriceInput, setMaxPriceInput] = useState(null);
  const [sortBy, setSortBy] = useState('');
  const [activeFilters, setActiveFilters] = useState([]);

  const setCurrentPageAndNavigate = (pageNumber) => {
    setCurrentPage(pageNumber);
  };


const handleSearch = (e) => {
     e.preventDefault();

    setCurrentPage(1);
    const searchQuery = e.target.elements.searchQuery.value;
    setSearchQuery(searchQuery);
   if (searchQuery) {
       // 이미 해당 필터 유형이 존재하는지 확인
       const existingFilter = activeFilters.find((filter) => filter.type === '검색어');
       if (existingFilter) {
         // 이미 존재하는 필터 유형이면 값을 업데이트
         setActiveFilters((prevFilters) =>
           prevFilters.map((filter) =>
             filter.type === '검색어' ? { type: '검색어', value: searchQuery } : filter
           )
         );
       } else {
         // 존재하지 않는 필터 유형이면 새로 추가
         setActiveFilters((prevFilters) => [...prevFilters, { type: '검색어', value: searchQuery }]);
       }
     }
    fetchData();
  };



const handleSortByChange = (e) => {
  const selectedSortBy = e.target.value;
  setSortBy(selectedSortBy);

  if (selectedSortBy) {
    let filterValue;
    switch(selectedSortBy) {
      case 'itemDesc':
        filterValue = '최신순';
        break;
      case 'priceAsc':
        filterValue = '낮은 가격순';
        break;
      case 'priceDesc':
        filterValue = '높은 가격순';
        break;
      default:
        filterValue = '';
        break;
    }

    const existingFilter = activeFilters.find((filter) => filter.type === '정렬 방식');
    if (existingFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '정렬 방식' ? { type: '정렬 방식', value: filterValue } : filter
        )
      );
    } else {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '정렬 방식', value: filterValue }]);
    }
  }
  fetchData();
};

const searchByPrice = (e) => {
  e.preventDefault();
  setCurrentPage(1);

  const minPrice = e.target.elements.minPrice.value;
  const maxPrice = e.target.elements.maxPrice.value;
  setMinPrice(parseInt(minPrice));
  setMaxPrice(parseInt(maxPrice));
   const existingMinPriceFilter = activeFilters.find((filter) => filter.type === '최소 가격');
     const existingMaxPriceFilter = activeFilters.find((filter) => filter.type === '최대 가격');

     if (minPrice && existingMinPriceFilter) {
       // 이미 존재하는 minPrice 필터 유형이면 값을 업데이트
       setActiveFilters((prevFilters) =>
         prevFilters.map((filter) =>
           filter.type === '최소 가격' ? { type: '최소 가격', value: minPrice } : filter
         )
       );
     } else if (minPrice) {
       // 존재하지 않는 minPrice 필터 유형이면 새로 추가
       setActiveFilters((prevFilters) => [...prevFilters, { type: '최소 가격', value: minPrice }]);
     }

     if (maxPrice && existingMaxPriceFilter) {
       // 이미 존재하는 maxPrice 필터 유형이면 값을 업데이트
       setActiveFilters((prevFilters) =>
         prevFilters.map((filter) =>
           filter.type === '최대 가격' ? { type: '최대 가격', value: maxPrice } : filter
         )
       );
     } else if (maxPrice) {
       // 존재하지 않는 maxPrice 필터 유형이면 새로 추가
       setActiveFilters((prevFilters) => [...prevFilters, { type: '최대 가격', value: maxPrice }]);
     }
  fetchData();
};

    useEffect(() => {
         fetchData();
       }, [categoryId, currentPage, selectedSubCategory, searchQuery, minPrice, maxPrice, sortBy]);

  const fetchData = async () => {
      let endpoint;
      if (selectedCategory) {
        endpoint = `/api/boardList/category/${selectedSubCategory}`;
      } else {
        endpoint = `/api/boardList`;
      }
      if (searchQuery) {
          endpoint += `/searchQuery/${searchQuery}`;
      }
     if (minPrice) {
          endpoint += `/minPrice/${minPrice}`;
        }
        if (maxPrice) {
          endpoint += `/maxPrice/${maxPrice}`;
        }
        if(sortBy){
          endpoint += `/sortBy/${sortBy}`;
        }
      endpoint += `/${currentPage}`;
      await axios
        .get(endpoint)
        .then((response) => {
          let top = [...response.data.top];
          let sub = [...response.data.sub];
          let boards = [...response.data.tradeBoards.content];
          let total = response.data.totalPages;
          setTopCategories(top);
          setSubCategories(sub);
          setTradeBoards(boards);
          setTotalPages(total);
        })
        .catch((error) => {
          console.error('Error fetching data:', error);
        });
  };


     const removeFilter = (filterType) => {
        setActiveFilters((prevFilters) => prevFilters.filter((filter) => filter.type !== filterType));

        // 각 필터 유형에 따라 관련 state를 초기화합니다.
        switch (filterType) {
          case '검색어':
            setSearchQuery('');
            break;
          case '최소 가격':
            setMinPrice(null);
            break;
          case '최대 가격':
            setMaxPrice(null);
            break;
          case '정렬 방식':
            setSortBy('');
            break;
          case '카테고리':
            setSelectedSubCategory(null);
            break;
          default:
            break;
        }

        fetchData();
      };


  const renderTopCategories = topCategories.map((category) => (
    <Nav.Item key={category.id}>
      <Button
        onClick={() => handleTopCategoryClick(category.id)}
        active={category.id === selectedCategory}
        className="category-link"
      >
        {category.name}
      </Button>
    </Nav.Item>
  ));

  const renderSubCategories = subCategories
    .filter((subcategory) => subcategory.topCategory.id === selectedCategory)
    .map((subcategory) => (
      <Nav.Item key={subcategory.id}>
        <Button
          onClick={() => handleSubCategoryClick(subcategory.id, subcategory.name)}
          className={`sub-category-link ${selectedSubCategory === subcategory.id ? 'active' : ''}`}
        >
          {subcategory.name}
        </Button>
      </Nav.Item>
    ));

  const handleTopCategoryClick = (categoryId) => {
    setSelectedCategory(categoryId === selectedCategory ? null : categoryId);
    setSelectedSubCategory(null);
  };

  const handleSubCategoryClick = (subCategoryId, subCategoryName) => {
    setSelectedSubCategory(subCategoryId);
    const existingSubCategoryFilter = activeFilters.find((filter) => filter.type === '카테고리');
    if (existingSubCategoryFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '카테고리' ? { type: '카테고리', value: subCategoryName } : filter
        )
      );
    } else {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '카테고리', value: subCategoryName }]);
    }
    setCurrentPage(1);
  };


  return (
    <Container fluid>
    <form onSubmit={handleSearch}>
       <Row className="d-flex justify-content-center">
            <Col sm={2}>
                <input type="text" name="searchQuery" value={searchQueryInput} onChange={(e) => setSearchQueryInput(e.target.value)} />
              <button type="submit">
                <BiSearch />
              </button>
            </Col>
          </Row>
    </form>
    <Row className="justify-content-start">
     <div>
          {activeFilters.map((filter) => (
            <div key={filter.type}>
              <span>
                {filter.type} : {filter.value}
              </span>
              <button onClick={() => removeFilter(filter.type)}>x</button>
            </div>
          ))}
        </div>
    </Row>
    <Row className="justify-content-end">
     <Col md={2}>
       <Form.Select className="sortBy" onChange={handleSortByChange}>
         <option value="">정렬 방식</option>
         <option value="itemDesc">최신순</option>
         <option value="priceAsc">낮은 가격순</option>
         <option value="priceDesc">높은 가격순</option>
       </Form.Select>
     </Col>
    </Row>

      <Row>
        <Col sm={1}>
          <Nav className="flex-column">
            <div>
              <Button href={`/boardList`} className="category-link">
                전체
              </Button>
            </div>
            {renderTopCategories}
          </Nav>
        </Col>
        <Col sm={1} className={selectedCategory ? 'show-subcategory' : 'hide-subcategory'}>
          {selectedCategory && <Nav className="flex-column">{renderSubCategories}</Nav>}
        </Col>
        <Col sm={9} className={selectedCategory ? 'pushed-content' : ''}>
          {tradeBoards.length > 0 ? (
            <div className="sell-boards-container">
              {tradeBoards.map((board) => {
                let timeAgo = formatDistanceToNow(parseISO(board.createdDate), { addSuffix: true, locale: ko });

                return (
                  <div key={board.id} className="sell-board">
                    <Nav.Link href={`/boardDetail/${board.id}`}>
                      <div className="sell-board-image">
                        <img
                          src={`https://storage.cloud.google.com/reboot-minty-storage/${board.thumbnail}`}
                          alt={board.title}
                          className="sell-board-image"
                          effect="opacity"
                        />
                      </div>
                      <div className="sell-board-tt">{board.title}</div>
                      <div className="sell-board-p">{board.price.toLocaleString()} 원</div>
                      <Row>
                        <Col>
                          <div className="sell-board-nn">
                            <span>🤍 {board.interesting}</span>
                          </div>
                        </Col>
                        <Col>
                          <div className="sell-board-cd">
                            <span>{timeAgo}</span>
                          </div>
                        </Col>
                      </Row>
                      <div className="sell-board-ul">
                        <span> {board.userLocation.address}</span>
                      </div>
                    </Nav.Link>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="no-results-message">게시물이 없습니다.</div>
          )}
          <div className="pagination-container">
            <Pagination totalPages={totalPages} currentPage={currentPage} setCurrentPage={setCurrentPageAndNavigate} />
          </div>
        </Col>
           <form onSubmit={searchByPrice}>
          <Col sm={2}>
               <input type="number" name="minPrice" placeholder="최소 가격" value={minPriceInput} onChange={(e) => setMinPriceInput(e.target.value)} />
               <input type="number" name="maxPrice" placeholder="최대 가격" value={maxPriceInput} onChange={(e) => setMaxPriceInput(e.target.value)} />
               <button type="submit">
                 검색
               </button>
               <button onClick={() => { setMinPriceInput(''); setMaxPriceInput(''); setMinPrice(null); setMaxPrice(null); fetchData(); }}>
                     가격 필터 초기화
                   </button>
             </Col>
        </form>
      </Row>
    </Container>
  );
}

export default BoardList;
